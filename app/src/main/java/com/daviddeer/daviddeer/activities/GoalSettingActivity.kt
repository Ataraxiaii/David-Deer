package com.daviddeer.daviddeer.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.adapters.GoalAdapter
import com.daviddeer.daviddeer.util.StepDbHelper

class GoalSettingActivity : AppCompatActivity() {
    private lateinit var dbHelper: StepDbHelper
    private lateinit var etPlanName: EditText
    private lateinit var etPlanSteps: EditText
    private lateinit var adapter: GoalAdapter
    private lateinit var recyclerView: RecyclerView

    // Tracks the name of the plan currently being edited
    private var editingPlanName: String? = null

    // Stores raw data retrieved from the database
    private var presetList = mutableListOf<Pair<String, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_setting)

        dbHelper = StepDbHelper(this)

        // Initialize UI components
        recyclerView = findViewById(R.id.rvGoalPresets)
        etPlanName = findViewById(R.id.etNewPlanName)
        etPlanSteps = findViewById(R.id.etNewPlanSteps)
        val btnAdd = findViewById<Button>(R.id.btnAddNewPlan)
        val btnBack = findViewById<ImageView>(R.id.backButton)

        // 1. Set LayoutManager (Mandatory for RecyclerView to display items)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 2. Initialize Adapter with click and long-press callbacks
        adapter = GoalAdapter(
            presetList,
            onClick = { selected ->
                enterEditMode(selected.first, selected.second, btnAdd)
            },
            onLongClick = { selected ->
                showDeleteDialog(selected.first)
            }
        )
        recyclerView.adapter = adapter

        // Load initial data from database
        refreshData()

        // Handle Add/Update button logic
        btnAdd.setOnClickListener {
            val name = etPlanName.text.toString().trim()
            val stepsStr = etPlanSteps.text.toString().trim()
            val steps = stepsStr.toIntOrNull() ?: 10000

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a plan name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editingPlanName != null) {
                // Update existing record
                dbHelper.updatePreset(editingPlanName!!, steps)
                Toast.makeText(this, "Plan updated!", Toast.LENGTH_SHORT).show()
            } else {
                // Insert new record
                dbHelper.addPreset(name, steps)
                Toast.makeText(this, "New plan added!", Toast.LENGTH_SHORT).show()
            }

            // Reset UI state and refresh the list
            exitEditMode(btnAdd)
            refreshData()
        }

        btnBack.setOnClickListener { finish() }
    }

    /**
     * Shows a confirmation dialog before deleting a plan
     */
    private fun showDeleteDialog(name: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Plan")
            .setMessage("Do you want to delete '$name'?")
            .setPositiveButton("Delete") { _, _ ->
                dbHelper.deletePreset(name)
                refreshData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Fills the input fields with selected plan data and changes button to Update mode
     */
    private fun enterEditMode(name: String, steps: Int, actionButton: Button) {
        editingPlanName = name
        etPlanName.setText(name)
        // Disable name editing as it acts as the unique identifier/key
        etPlanName.isEnabled = false
        etPlanSteps.setText(steps.toString())

        actionButton.text = "UPDATE PLAN"
        // Change color to indicate active editing mode
        actionButton.setBackgroundColor(android.graphics.Color.parseColor("#2D3436"))
    }

    /**
     * Clears input fields and restores button to Add mode
     */
    private fun exitEditMode(actionButton: Button) {
        editingPlanName = null
        etPlanName.text.clear()
        etPlanName.isEnabled = true
        etPlanSteps.text.clear()

        actionButton.text = "ADD NEW PLAN"
        actionButton.setBackgroundColor(android.graphics.Color.parseColor("#DFA135"))
    }

    /**
     * Fetches latest presets from DB and notifies the adapter to refresh the UI
     */
    private fun refreshData() {
        presetList.clear()
        presetList.addAll(dbHelper.getAllPresets())
        adapter.updateData(presetList)
    }
}