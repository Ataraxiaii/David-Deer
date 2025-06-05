package com.daviddeer.daviddeer.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.adapters.GuidePagerAdapter

class GuideActivity : ComponentActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotContainer: LinearLayout
    private lateinit var dots: Array<ImageView?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        viewPager = findViewById(R.id.viewPager)
        val adapter = GuidePagerAdapter(this)
        viewPager.adapter = adapter

        dotContainer = findViewById(R.id.dotContainer)

        // Initialize dots to show current page
        viewPager.post {
            addDots(adapter.itemCount, viewPager.currentItem)
        }

        // Add dots for initial position (0)
        addDots(adapter.itemCount, 0)

        // Register page change callback to update dots
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                addDots(adapter.itemCount, position)
            }
        })

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    /**
     * Adds page indicator dots to the layout
     * @param count Total number of pages
     * @param currentPosition Current page position
     */
    private fun addDots(count: Int, currentPosition: Int) {
        dotContainer.removeAllViews()
        dots = arrayOfNulls(count)

        // Create a dot for each page
        for (i in 0 until count) {
            val dot = ImageView(this).apply {
                val drawable = if (i == currentPosition) R.drawable.dot_active else R.drawable.dot_inactive
                setImageDrawable(ContextCompat.getDrawable(this@GuideActivity, drawable))

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                layoutParams = params

                // Set click listener to navigate to corresponding page
                setOnClickListener {
                    viewPager.currentItem = i
                }
            }

            dots[i] = dot
            dotContainer.addView(dot)
        }
    }
}