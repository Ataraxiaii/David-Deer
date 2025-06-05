package com.daviddeer.daviddeer

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.daviddeer.daviddeer.adapter.GuidePagerAdapter

class GuideActivity : ComponentActivity() {

    private lateinit var dotContainer: LinearLayout
    private lateinit var dots: Array<ImageView?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = GuidePagerAdapter(this)
        viewPager.adapter = GuidePagerAdapter(this)

        dotContainer = findViewById(R.id.dotContainer)
        addDots(adapter.itemCount, 0) // 初始化圆点

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                addDots(adapter.itemCount, position)
            }
        })

        // 点击返回主界面
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    // 翻页的底下圆点提示
    private fun addDots(count: Int, currentPosition: Int) {
        dotContainer.removeAllViews()
        dots = arrayOfNulls(count)

        for (i in 0 until count) {
            dots[i] = ImageView(this).apply {
                val drawable = if (i == currentPosition) R.drawable.dot_active else R.drawable.dot_inactive
                setImageDrawable(ContextCompat.getDrawable(this@GuideActivity, drawable))

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                layoutParams = params
            }
            dotContainer.addView(dots[i])
        }
    }
}
