package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.R


// Level 1: Card flipping memory game
class LevelOneActivity : ComponentActivity() {

    private lateinit var cards: List<ImageButton>
    private lateinit var tvRules: TextView

    private lateinit var cardImages: List<Int>
    private var firstSelected: ImageButton? = null
    private var pairsFound = 0
    private val totalPairs = 4  // Total 4 pairs of cards

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_one)

        // Back button
        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()  // Return to previous screen (GameActivity)
        }

        tvRules = findViewById(R.id.tvRules)
        tvRules.text = "Find all the paired beast cards!"

        // Initialize card resources (4 pairs of images)
        cardImages = listOf(
            R.drawable.daviddeer, R.drawable.pixiu, R.drawable.pixiu, R.drawable.jingwei,
            R.drawable.daviddeer, R.drawable.jingwei, R.drawable.pulao, R.drawable.pulao
        ).shuffled()

        // Get 8 card buttons
        cards = listOf(
            findViewById(R.id.card1),
            findViewById(R.id.card2),
            findViewById(R.id.card3),
            findViewById(R.id.card4),
            findViewById(R.id.card5),
            findViewById(R.id.card6),
            findViewById(R.id.card7),
            findViewById(R.id.card8)
        )

        cards.forEachIndexed { index, card ->
            card.setImageResource(R.drawable.beastlocked)  // Card back image, using locked beast image
            card.tag = cardImages[index]  // Temporarily store corresponding image resource ID

            card.setOnClickListener {
                flipCard(card, index)
            }
        }

        // If already passed, show dialog (does not affect continuing game)
        if (BeastRepository.getBeastById(6)?.isUnlocked == true) {
            showAlreadyPassedDialog()
        }
    }

    // Flip card
    private fun flipCard(card: ImageButton, index: Int) {
        val imageRes = cardImages[index]

        // If already flipped or matched, do not allow flipping again
        if (card.drawable.constantState == resources.getDrawable(imageRes, null).constantState) return

        card.setImageResource(imageRes)

        if (firstSelected == null) {
            firstSelected = card
        } else {
            // Second flip, check if matches
            val firstIndex = cards.indexOf(firstSelected!!)
            val firstImage = cardImages[firstIndex]

            if (firstImage == imageRes && firstSelected != card) {
                // Successful match
                firstSelected?.isEnabled = false
                card.isEnabled = false
                pairsFound++
                firstSelected = null

                if (pairsFound == totalPairs) {
                    onLevelOnePassed()
                }
            } else {
                // Match failed, flip back with delay
                Handler(Looper.getMainLooper()).postDelayed({
                    firstSelected?.setImageResource(R.drawable.beastlocked)
                    card.setImageResource(R.drawable.beastlocked)
                    firstSelected = null
                }, 200)
            }
        }
    }

    // Dialog shown on replay
    private fun showAlreadyPassedDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_level_repeated, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes.gravity = android.view.Gravity.CENTER
        }

        dialogView.findViewById<Button>(R.id.btnOK).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Unlock beast collection
    private fun onLevelOnePassed() {
        // Unlock beasts 6 to 9
        BeastRepository.unlockBeastsByIds(listOf(6, 7, 8, 9))
        // Save unlock state after each pass
        BeastRepository.saveUnlockedState(this)

        // Load custom layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_level_complete, null)

        // Show dialog centered (via Window attributes)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent) // Transparent background
            attributes.gravity = android.view.Gravity.CENTER // Center dialog
        }

        // OK button click event
        dialogView.findViewById<Button>(R.id.btnOK).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        dialog.show()
    }
}
