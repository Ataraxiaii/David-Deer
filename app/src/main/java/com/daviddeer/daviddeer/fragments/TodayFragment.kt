package com.daviddeer.daviddeer.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.activities.GoalSettingActivity
import com.daviddeer.daviddeer.util.StepDbHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodayFragment : Fragment() {
    private lateinit var dbHelper: StepDbHelper
    private val prefs by lazy { requireContext().getSharedPreferences("StepPrefs", Context.MODE_PRIVATE) }

    // Member variables for easier access across different methods
    private lateinit var planSpinner: Spinner
    private var currentPresets: List<Pair<String, Int>> = listOf()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { updateUI() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = StepDbHelper(requireContext())

        planSpinner = view.findViewById(R.id.planSpinner)
        val btnApply = view.findViewById<Button>(R.id.btnApplyPlan)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEditPlans)

        // 1. Initialize Spinner data from database
        refreshSpinnerData()

        // 2. Handle Plan Switch button click
        btnApply.setOnClickListener {
            if (currentPresets.isNotEmpty()) {
                val selectedIndex = planSpinner.selectedItemPosition
                val selected = currentPresets[selectedIndex]

                // Save selected goal and plan name to SharedPreferences
                prefs.edit()
                    .putInt("step_goal", selected.second)
                    .putString("active_plan_name", selected.first)
                    .apply()

                Toast.makeText(requireContext(), "Plan Switched: ${selected.first}", Toast.LENGTH_SHORT).show()
                updateUI()
            }
        }

        // 3. Navigate to the Plan Management/Edit screen
        btnEdit.setOnClickListener {
            startActivity(Intent(requireContext(), GoalSettingActivity::class.java))
        }

        updateUI()
    }

    /**
     * Refreshes the Spinner with current plans from the database.
     * Called on creation and whenever the fragment resumes.
     */
    private fun refreshSpinnerData() {
        currentPresets = dbHelper.getAllPresets()
        val names = currentPresets.map { "${it.first} (${it.second} steps)" }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planSpinner.adapter = adapter

        // Automatically select the plan currently in use
        val activeName = prefs.getString("active_plan_name", "Default")
        val index = currentPresets.indexOfFirst { it.first == activeName }
        if (index != -1) planSpinner.setSelection(index)
    }

    /**
     * Optional: Displays a dialog for quick goal switching
     */
    private fun showGoalSelectionDialog() {
        val presets = dbHelper.getAllPresets()
        val names = presets.map { "${it.first} (${it.second})" }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Switch Exercise Plan")
            .setItems(names) { _, which ->
                val selected = presets[which]
                // Store active step goal in SharedPreferences for tracking logic
                requireContext().getSharedPreferences("StepPrefs", Context.MODE_PRIVATE).edit()
                    .putInt("step_goal", selected.second)
                    .putString("active_plan_name", selected.first)
                    .apply()
                updateUI()
            }
            .setNeutralButton("Edit Plans") { _, _ ->
                startActivity(Intent(requireContext(), GoalSettingActivity::class.java))
            }
            .show()
    }

    /**
     * Main UI update method to refresh step counts, progress bars, and stats
     */
    private fun updateUI() {
        val view = view ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val steps = dbHelper.getSteps(today)

        // Read currently active goal and name from SharedPreferences
        // Default values set to 10000 steps and "Default" plan
        val goal = prefs.getInt("step_goal", 10000)
        val planName = prefs.getString("active_plan_name", "Default")

        // Update step count and circular progress bar
        view.findViewById<TextView>(R.id.stepCountTextView).text = steps.toString()
        view.findViewById<ProgressBar>(R.id.progressBar).apply {
            this.max = if (goal > 0) goal else 10000 // Prevent max from being 0
            this.progress = steps
        }

        // Display the name of the current plan and its goal
        view.findViewById<TextView>(R.id.goalTextView).text = "$planName Goal: $goal"

        // Calculate and display distance (approx. 0.75m per step) and calories (approx. 0.04kcal per step)
        val km = (steps * 0.00075)
        view.findViewById<TextView>(R.id.distanceTextView).text = String.format("%.2f km", km)
        view.findViewById<TextView>(R.id.calorieTextView).text = "${(steps * 0.04).toInt()} kcal"

        updateBeastStatus(steps, goal)
    }

    /**
     * Updates the Beast image and status text based on current goal progress
     */
    private fun updateBeastStatus(steps: Int, goal: Int) {
        val view = view ?: return
        val progress = if (goal > 0) (steps.toFloat() / goal).coerceAtMost(1.0f) else 0f

        val statusTv = view.findViewById<TextView>(R.id.beastStatusTv)
        val beastImg = view.findViewById<ImageView>(R.id.beastImageView)

        // 1. Smooth Alpha transition: ranges from 0.3 (inactive) to 1.0 (full progress)
        beastImg.alpha = 0.3f + (progress * 0.7f)

        when {
            progress >= 1.0f -> {
                statusTv.text = "Legendary!"
                statusTv.setTextColor(Color.parseColor("#DFA135"))

                beastImg.setImageResource(R.drawable.zhuque_perfect)

                // Add a subtle scale-up animation for reaching the goal
                beastImg.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start()
            }
            progress >= 0.5f -> {
                statusTv.text = "Active Wings!"
                statusTv.setTextColor(Color.BLACK)

                beastImg.setImageResource(R.drawable.zhuque_go)

                beastImg.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start()
            }
            else -> {
                statusTv.text = "Hungry..."
                statusTv.setTextColor(Color.GRAY)

                beastImg.setImageResource(R.drawable.zhuque_hungery)

                beastImg.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start()
            }
        }
    }

    // Lifecycle Management: Ensure data is fresh when returning to the app
    override fun onResume() {
        super.onResume()
        /* Register Broadcast Receiver here if needed */
        refreshSpinnerData()
        updateUI()
    }
}