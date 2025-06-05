package com.daviddeer.daviddeer.activities

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.adapters.BeastAdapter
import com.daviddeer.daviddeer.data.BeastRepository

// Bestiary screen
class BestiaryActivity : ComponentActivity() {
    // Beast data
    private lateinit var recyclerView: RecyclerView
    private lateinit var beastAdapter: BeastAdapter
    private val beastList = BeastRepository.getBeasts() // Simulated beast data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bestiary) // Set adapter

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
}