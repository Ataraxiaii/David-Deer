package com.daviddeer.daviddeer

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
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.util.LoginManager

class MainActivity : ComponentActivity() {
    private val selectBeastLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedBeastImage = result.data?.getIntExtra("selectedBeastImage", 0)
            if (selectedBeastImage != null && selectedBeastImage != 0) {
                findViewById<ImageView>(R.id.mainImage).setImageResource(selectedBeastImage)
            }
        }
    }

    private var originalX = 0f
    private var originalY = 0f
    private var originalScaleX = 1f
    private var originalScaleY = 1f
    private var isDragging = false
    private var dragCorner = 0 // 0:未选中 1:左上 2:右上 3:右下 4:左下
    private val cornerDetectionThreshold = 60 // 角落检测阈值(像素)
    private var initialTouchPoint = PointF()
    private var initialScaleX = 1f
    private var initialScaleY = 1f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 加载数据
        BeastRepository.loadAllStates(this)

        // 加载已选择的灵兽图片
        loadSelectedBeast()

        // 设置按钮点击事件
        setupButtonClickListeners()

        // 设置图片拉伸和点击效果
        val mainImage = findViewById<ImageView>(R.id.mainImage)
        mainImage.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 记录原始状态
                    originalX = view.x
                    originalY = view.y
                    originalScaleX = view.scaleX
                    originalScaleY = view.scaleY
                    initialScaleX = originalScaleX
                    initialScaleY = originalScaleY

                    // 获取当前缩放后的实际宽度和高度
                    val currentScaleX = view.scaleX
                    val currentScaleY = view.scaleY
                    val width = view.width * currentScaleX
                    val height = view.height * currentScaleY

                    val touchX = event.x
                    val touchY = event.y

                    // 打印触摸位置信息，用于调试
                    Log.d("MainActivity", "Touch at: ($touchX, $touchY), View size: ($width, $height)")

                    // 判断是否触摸在角落区域
                    dragCorner = when {
                        touchX < cornerDetectionThreshold && touchY < cornerDetectionThreshold -> 1 // 左上
                        touchX > width - cornerDetectionThreshold && touchY < cornerDetectionThreshold -> 2 // 右上
                        touchX > width - cornerDetectionThreshold && touchY > height - cornerDetectionThreshold -> 3 // 右下
                        touchX < cornerDetectionThreshold && touchY > height - cornerDetectionThreshold -> 4 // 左下
                        else -> 0 // 未触摸角落
                    }

                    // 如果触摸在角落，开始拖拽
                    if (dragCorner != 0) {
                        Log.d("MainActivity", "Dragging corner: $dragCorner")
                        isDragging = true
                        initialTouchPoint.set(event.rawX, event.rawY)
                        return@setOnTouchListener true
                    }

                    // 如果未触摸角落，触发点击事件
                    Log.d("MainActivity", "Triggering click event")
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) {
                        val dx = event.rawX - initialTouchPoint.x
                        val dy = event.rawY - initialTouchPoint.y

                        // 获取当前缩放后的实际宽度和高度（实时更新）
                        val currentScaleX = view.scaleX
                        val currentScaleY = view.scaleY
                        val halfWidth = (view.width * currentScaleX) / 2
                        val halfHeight = (view.height * currentScaleY) / 2

                        var newScaleX = initialScaleX
                        var newScaleY = initialScaleY

                        when (dragCorner) {
                            1 -> { // 左上
                                newScaleX = initialScaleX - dx / halfWidth
                                newScaleY = initialScaleY - dy / halfHeight
                            }
                            2 -> { // 右上
                                newScaleX = initialScaleX + dx / halfWidth
                                newScaleY = initialScaleY - dy / halfHeight
                            }
                            3 -> { // 右下
                                newScaleX = initialScaleX + dx / halfWidth
                                newScaleY = initialScaleY + dy / halfHeight
                            }
                            4 -> { // 左下
                                newScaleX = initialScaleX - dx / halfWidth
                                newScaleY = initialScaleY + dy / halfHeight
                            }
                        }

                        // 限制缩放范围（可根据需求调整）
                        newScaleX = newScaleX.coerceIn(0.5f, 2.0f)
                        newScaleY = newScaleY.coerceIn(0.5f, 2.0f)

                        view.scaleX = newScaleX
                        view.scaleY = newScaleY

                        // 更新初始触摸点，实现连续拖拽效果
                        initialTouchPoint.set(event.rawX, event.rawY)

                        return@setOnTouchListener true
                    }
                    return@setOnTouchListener false
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isDragging) {
                        isDragging = false
                        dragCorner = 0

                        // 创建回弹动画，使用原始缩放比例（originalScaleX/Y）
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
        // 添加单独的点击监听器，确保点击事件能被触发
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
        // 跳转图鉴界面
        findViewById<ImageButton>(R.id.btnBestiary).setOnClickListener {
            startActivity(Intent(this, BestiaryActivity::class.java))
        }

        // 跳转地图界面
        findViewById<ImageButton>(R.id.btnMap).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // 跳转游戏界面
        findViewById<ImageButton>(R.id.btnGame).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        // 跳转计步界面
        findViewById<ImageButton>(R.id.btnStep).setOnClickListener {
            startActivity(Intent(this, StepsActivity::class.java))
        }

        // 设置新增按钮的点击事件
        findViewById<ImageButton>(R.id.selectBeastButton).setOnClickListener {
            val intent = Intent(this, SelectBeastActivity::class.java)
            selectBeastLauncher.launch(intent)
        }

        // 说明书跳转
        findViewById<ImageButton>(R.id.btnGuide).setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }

        // 设置登出按钮的点击事件
        findViewById<ImageButton>(R.id.btnLogout).setOnClickListener {
            // 登出用户
            LoginManager.logout(this)

            // 保存当前状态
            BeastRepository.saveAllStates(this)

            // 显示登出成功消息
            Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show()

            // 返回开始界面并清除所有上层Activity
            val intent = Intent(this, StartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun createJellyAnimation(view: View): AnimatorSet {
        // 创建缩放动画
        val scaleX1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f)
        val scaleY1 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f)
        val scaleX2 = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 0.9f)
        val scaleY2 = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1.1f)
        val scaleX3 = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1.05f)
        val scaleY3 = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 0.95f)
        val scaleX4 = ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1f)
        val scaleY4 = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f)

        // 设置动画持续时间
        scaleX1.duration = 100
        scaleY1.duration = 100
        scaleX2.duration = 100
        scaleY2.duration = 100
        scaleX3.duration = 100
        scaleY3.duration = 100
        scaleX4.duration = 100
        scaleY4.duration = 100

        // 设置插值器，使动画有弹性效果
        scaleX2.interpolator = OvershootInterpolator(2f)
        scaleY2.interpolator = OvershootInterpolator(2f)

        // 创建动画集
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleX1).with(scaleY1)
        animatorSet.play(scaleX2).with(scaleY2).after(scaleX1)
        animatorSet.play(scaleX3).with(scaleY3).after(scaleX2)
        animatorSet.play(scaleX4).with(scaleY4).after(scaleX3)

        // 确保动画结束后保持在最终状态
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