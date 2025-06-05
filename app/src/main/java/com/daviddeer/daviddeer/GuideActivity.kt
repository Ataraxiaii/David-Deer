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

        // 初始化显示当前页圆点
        viewPager.post {
            addDots(adapter.itemCount, viewPager.currentItem)
        }

        addDots(adapter.itemCount, 0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                addDots(adapter.itemCount, position)
            }
        })

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    // 翻页点点
    private fun addDots(count: Int, currentPosition: Int) {
        dotContainer.removeAllViews()
        dots = arrayOfNulls(count)

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

                // 设置点击事件：点击跳转到对应页
                setOnClickListener {
                    viewPager.currentItem = i
                }
            }

            dots[i] = dot
            dotContainer.addView(dot)
        }
    }
}
