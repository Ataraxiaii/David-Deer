package com.daviddeer.daviddeer.activities

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.util.LoginManager

class MainActivity : ComponentActivity() {
    // Activity result launcher for selecting beasts
    private val selectBeastLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedBeastImage = result.data?.getIntExtra("selectedBeastImage", 0)
            if (selectedBeastImage != null && selectedBeastImage != 0) {
                findViewById<ImageView>(R.id.mainImage).setImageResource(selectedBeastImage)
            }
        }
    }

    // Variables for image manipulation state
    private var originalX = 0f
    private var originalY = 0f
    private var originalScaleX = 1f
    private var originalScaleY = 1f
    private var isDragging = false
    private var dragCorner = 0 // 0:not selected 1:top-left 2:top-right 3:bottom-right 4:bottom-left
    private val cornerDetectionThreshold = 60 // Corner detection threshold in pixels
    private var initialTouchPoint = PointF()
    private var initialScaleX = 1f
    private var initialScaleY = 1f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load application data
        BeastRepository.loadAllStates(this)

        // Load previously selected beast image
        loadSelectedBeast()

        // Set up button click listeners
        setupButtonClickListeners()

        // Configure image stretching and click effects
        val mainImage = findViewById<ImageView>(R.id.mainImage)
        mainImage.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Record initial state
                    originalX = view.x
                    originalY = view.y
                    originalScaleX = view.scaleX
                    originalScaleY = view.scaleY
                    initialScaleX = originalScaleX
                    initialScaleY = originalScaleY

                    // Get current scaled dimensions
                    val currentScaleX = view.scaleX
                    val currentScaleY = view.scaleY
                    val width = view.width * currentScaleX
                    val height = view.height * currentScaleY

                    val touchX = event.x
                    val touchY = event.y

                    // Log touch position for debugging
                    Log.d("MainActivity", "Touch at: ($touchX, $touchY), View size: ($width, $height)")

                    // Determine if touch is in corner regions
                    dragCorner = when {
                        touchX < cornerDetectionThreshold && touchY < cornerDetectionThreshold -> 1 // top-left
                        touchX > width - cornerDetectionThreshold && touchY < cornerDetectionThreshold -> 2 // top-right
                        touchX > width - cornerDetectionThreshold && touchY > height - cornerDetectionThreshold -> 3 // bottom-right
                        touchX < cornerDetectionThreshold && touchY > height - cornerDetectionThreshold -> 4 // bottom-left
                        else -> 0 // not in corner
                    }

                    // If touching corner, start dragging
                    if (dragCorner != 0) {
                        Log.d("MainActivity", "Dragging corner: $dragCorner")
                        isDragging = true
                        initialTouchPoint.set(event.rawX, event.rawY)
                        return@setOnTouchListener true
                    }

                    // If not touching corner, trigger click event
                    Log.d("MainActivity", "Triggering click event")
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) {
                        val dx = event.rawX - initialTouchPoint.x
                        val dy = event.rawY - initialTouchPoint.y

                        // Get current scaled dimensions (updated in real-time)
                        val currentScaleX = view.scaleX
                        val currentScaleY = view.scaleY
                        val halfWidth = (view.width * currentScaleX) / 2
                        val halfHeight = (view.height * currentScaleY) / 2

                        var newScaleX = initialScaleX
                        var newScaleY = initialScaleY

                        when (dragCorner) {
                            1 -> { // top-left
                                newScaleX = initialScaleX - dx / halfWidth
                                newScaleY = initialScaleY - dy / halfHeight
                            }
                            2 -> { // top-right
                                newScaleX = initialScaleX + dx / halfWidth
                                newScaleY = initialScaleY - dy / halfHeight
                            }
                            3 -> { // bottom-right
                                newScaleX = initialScaleX + dx / halfWidth
                                newScaleY = initialScaleY + dy / halfHeight
                            }
                            4 -> { // bottom-left
                                newScaleX = initialScaleX - dx / halfWidth
                                newScaleY = initialScaleY + dy / halfHeight
                            }
                        }

                        // Limit scale range (adjustable as needed)
                        newScaleX = newScaleX.coerceIn(0.5f, 2.0f)
                        newScaleY = newScaleY.coerceIn(0.5f, 2.0f)

                        view.scaleX = newScaleX
                        view.scaleY = newScaleY

                        // Update initial touch point for continuous dragging
                        initialTouchPoint.set(event.rawX, event.rawY)

                        return@setOnTouchListener true
                    }
                    return@setOnTouchListener false
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isDragging) {
                        isDragging = false
                        dragCorner = 0

                        // Create bounce-back animation using original scale
                        val animX = ObjectAnimator.ofFloat(view, "scaleX", view.scaleX, originalScaleX)
                        val animY = ObjectAnimator.ofFloat(view, "scaleY", view.scaleY, originalScaleY)

                        animX.duration = 500
                        animY.duration = 500
                        animX.interpolator = OvershootInterpolator(2f)
                        animY.interpolator = OvershootInterpolator(2f)

                        AnimatorSet().apply {
                            playTogether(animX, animY)
                            start()
                        }

                        return@setOnTouchListener true
                    }
                    return@setOnTouchListener false
                }
            }
            return@setOnTouchListener false
        }
        // Add separate click listener to ensure click events work
        mainImage.setOnClickListener {
            Log.d("MainActivity", "Image clicked")
            createJellyAnimation(it).start()
        }
    }

    private fun loadSelectedBeast() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val imageResId = sharedPref.getInt("selected_beast_image", 0)

        if (imageResId != 0) {
            findViewById<ImageView>(R.id.mainImage).setImageResource(imageResId)
        }
    }

    private fun setupButtonClickListeners() {
        // Navigate to Bestiary
        findViewById<ImageButton>(R.id.btnBestiary).setOnClickListener {
            startActivity(Intent(this, BestiaryActivity::class.java))
        }

        // Navigate to Map
        findViewById<ImageButton>(R.id.btnMap).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // Navigate to Games
        findViewById<ImageButton>(R.id.btnGame).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        // Navigate to Step Counter
        findViewById<ImageButton>(R.id.btnStep).setOnClickListener {
            startActivity(Intent(this, StepsActivity::class.java))
        }

        // Set up beast selection button
        findViewById<ImageButton>(R.id.selectBeastButton).setOnClickListener {
            val intent = Intent(this, SelectBeastActivity::class.java)
            selectBeastLauncher.launch(intent)
        }

        // Navigate to Guide
        findViewById<ImageButton>(R.id.btnGuide).setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }

        // Set up logout button
        findViewById<ImageButton>(R.id.btnLogout).setOnClickListener {
            // Log out user
            LoginManager.logout(this)

            // Save current state
            BeastRepository.saveAllStates(this)

            // Show logout success message
            Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show()

            // Return to start screen and clear all activities
            val intent = Intent(this, StartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun createJellyAnimation(view: View): AnimatorSet {
        // Create scale animations
        val scaleX1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f)
        val scaleY1 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f)
        val scaleX2 = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 0.9f)
        val scaleY2 = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1.1f)
        val scaleX3 = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1.05f)
        val scaleY3 = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 0.95f)
        val scaleX4 = ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1f)
        val scaleY4 = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f)

        // Set animation durations
        scaleX1.duration = 100
        scaleY1.duration = 100
        scaleX2.duration = 100
        scaleY2.duration = 100
        scaleX3.duration = 100
        scaleY3.duration = 100
        scaleX4.duration = 100
        scaleY4.duration = 100

        // Set interpolators for elastic effect
        scaleX2.interpolator = OvershootInterpolator(2f)
        scaleY2.interpolator = OvershootInterpolator(2f)

        // Create animation set
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleX1).with(scaleY1)
        animatorSet.play(scaleX2).with(scaleY2).after(scaleX1)
        animatorSet.play(scaleX3).with(scaleY3).after(scaleX2)
        animatorSet.play(scaleX4).with(scaleY4).after(scaleX3)

        // Ensure view returns to original state after animation
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                view.scaleX = 1f
                view.scaleY = 1f
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        return animatorSet
    }
}