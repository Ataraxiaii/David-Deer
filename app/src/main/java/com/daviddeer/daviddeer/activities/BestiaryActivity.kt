package com.daviddeer.daviddeer.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.adapters.BeastAdapter
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.data.db.BeastDatabase
import com.daviddeer.daviddeer.data.db.BeastEntity

// Bestiary screen
class BestiaryActivity : ComponentActivity() {
    // Beast data
    private lateinit var recyclerView: RecyclerView
    private lateinit var beastAdapter: BeastAdapter
    private lateinit var searchView: SearchView
    private val beastList = BeastRepository.getBeasts() // Simulated beast data


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bestiary) // Set adapter

        initViews()
        initSearchListener()

        // Bestiary list
        recyclerView = findViewById(R.id.bestiaryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3-column grid
        beastAdapter = BeastAdapter(beastList, this)
        recyclerView.adapter = beastAdapter

        // Back button
        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()  // Return to previous screen (MainActivity)
        }
    }

    // search beast by name
    private fun initSearchListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) return false

                Thread {
                    val result = BeastDatabase
                        .getInstance(this@BestiaryActivity)
                        .beastDao()
                        .searchByName(query)

                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            showBeastDetailDialog(result[0])
                        } else {
                            Toast.makeText(
                                this@BestiaryActivity,
                                "Beast not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.start()

                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })
    }

    fun showBeastDetailDialog(beast: BeastEntity) {
        val view = layoutInflater.inflate(R.layout.dialog_beast_detail, null)

        // bind data
        view.findViewById<TextView>(R.id.dialogBeastName).text = beast.name
        view.findViewById<TextView>(R.id.dialogBeastStory).text = beast.story
        view.findViewById<ImageView>(R.id.dialogBeastImage)
            .setImageResource(beast.imageRes)

        // create dialog
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .create()

        // close button
        view.findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initViews() {
        searchView = findViewById(R.id.searchView)
        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()
        }
    }
}