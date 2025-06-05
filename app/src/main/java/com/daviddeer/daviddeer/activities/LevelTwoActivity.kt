package com.daviddeer.daviddeer.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.data.BeastRepository
import kotlin.random.Random


// Level Two is a fast reaction click game. Reaction time is 1 second, 10 points to pass. Player must finish the game within 13 seconds, otherwise fail.
class LevelTwoActivity : ComponentActivity() {
    private lateinit var target: ImageView
    private lateinit var tvScore: TextView
    private var score = 0
    private val maxScore = 10
    private var gameActive = true
    private var gameTimer: CountDownTimer? = null // Game timer
    private val handler = Handler(Looper.getMainLooper())
    private var hideTargetRunnable: Runnable? = null
    private var targetClickable = true // Control image click times to 1


    // Initialize beast images
    private val beastImages = listOf(
        R.drawable.qilin,
        R.drawable.whitetiger,
        R.drawable.xvanwu,
        R.drawable.yinglong
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_two)

        val btnStart = findViewById<Button>(R.id.btnStart)
        tvScore = findViewById(R.id.tvScore)
        target = findViewById(R.id.target)
        target.visibility = View.INVISIBLE

        // Back button to level selection screen
        val backButton = findViewById<ImageButton>(R.id.btnBackGamePage)
        backButton.setOnClickListener {
            gameTimer?.cancel()  // Stop game timer
            gameActive = false   // End game state
            finish()             // Return to previous screen (GameActivity)
        }

        btnStart.visibility = View.VISIBLE  // Show button when re-entering page

        // Start game button
        btnStart.setOnClickListener {
            btnStart.visibility = View.GONE  // Hide button after clicking
            startGame()
        }

        // Listener for clicking the image
        target.setOnClickListener {
            if (gameActive && target.visibility == View.VISIBLE && targetClickable) {
                targetClickable = false // Prevent repeated clicks
                target.isClickable = false // Immediately disable clicks

                // Click animation (shrink then enlarge)
                val scaleXDown = ObjectAnimator.ofFloat(target, View.SCALE_X, 1f, 0.8f)
                val scaleYDown = ObjectAnimator.ofFloat(target, View.SCALE_Y, 1f, 0.8f)
                val scaleXUp = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.8f, 1f)
                val scaleYUp = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.8f, 1f)

                val scaleDown = AnimatorSet().apply {
                    duration = 100
                    playTogether(scaleXDown, scaleYDown)
                }

                val scaleUp = AnimatorSet().apply {
                    duration = 100
                    playTogether(scaleXUp, scaleYUp)
                }

                val scaleSequence = AnimatorSet().apply {
                    playSequentially(scaleDown, scaleUp)
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            // After animation ends, hide and handle game logic
                            target.visibility = View.INVISIBLE
                            score++
                            tvScore.text = "Score: $score/$maxScore"

                            if (score >= maxScore) {
                                gameActive = false
                                onLevelTwoPassed()
                            } else {
                                showRandomTarget()
                            }
                        }
                    })
                }
                scaleSequence.start()
            }
        }

        // If already passed, show dialog (does not affect continuing the game)
        if (BeastRepository.getBeastById(10)?.isUnlocked == true) {
            showAlreadyPassedDialog()
        }
    }

    // Start game
    private fun startGame() {
        score = 0
        gameActive = true
        tvScore.text = "Score: 0/$maxScore"

        // Start 13 seconds countdown
        gameTimer?.cancel()
        gameTimer = object : CountDownTimer(13000, 1000) {
            val tvTimer = findViewById<TextView>(R.id.tvTimer)
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "Time: ${millisUntilFinished / 1000}s"
            } // Remaining time

            override fun onFinish() {
                if (gameActive) {
                    gameActive = false
                    onGameFailed()
                }
            }
        }.start()

        showRandomTarget()
    }

    // Game failed
    private fun onGameFailed() {
        gameTimer?.cancel()

        // Show fail dialog
        showFailDialog()
    }

    // Show fail dialog
    private fun showFailDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_level_fail, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes.gravity = android.view.Gravity.CENTER
        }

        // Set OK button click event: return to level selection screen
        dialogView.findViewById<Button>(R.id.btnOK).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        dialog.show()
    }

    // Show beast image appearing randomly
    private fun showRandomTarget() {
        if (!gameActive) return

        val screenWidth = resources.displayMetrics.widthPixels - 200
        val screenHeight = resources.displayMetrics.heightPixels
        val upperPadding = 300 // Approximately below button + score area
        val lowerPadding = 300 // Avoid bottom obstruction
        val availableHeight = screenHeight - upperPadding - lowerPadding
        val x = Random.nextInt(screenWidth)
        val y = upperPadding + Random.nextInt(availableHeight)

        target.setImageResource(beastImages.random()) // Randomly choose a beast image
        target.x = x.toFloat()
        target.y = y.toFloat()
        target.visibility = View.VISIBLE

        // Cancel previous hide task first
        hideTargetRunnable?.let { handler.removeCallbacks(it) }

        // Create new hide task: automatically disappear after 1 second
        hideTargetRunnable = Runnable {
            if (target.visibility == View.VISIBLE && gameActive) {
                target.visibility = View.INVISIBLE
                showRandomTarget()
            }
        }
        handler.postDelayed(hideTargetRunnable!!, 1000)

        // Restore clicking
        target.visibility = View.VISIBLE
        targetClickable = true // Restore clicking when new image appears
        target.isClickable = true  // Re-enable clicking
    }

    // Show dialog when replaying level
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

    // Unlock bestiary after passing level
    private fun onLevelTwoPassed() {
        gameTimer?.cancel()
        // Unlock beasts 10~13
        BeastRepository.unlockBeastsByIds(listOf(10, 11, 12, 13))
        // Save unlock state after each pass
        BeastRepository.saveUnlockedState(this)

        // Load custom layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_level_complete, null)

        // Center dialog display (via Window attributes)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent) // Transparent background
            attributes.gravity = android.view.Gravity.CENTER // Dialog centered
        }

        // Set OK button click event
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
