package com.daviddeer.daviddeer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.data.Beast
import com.daviddeer.daviddeer.data.BeastRepository

class SelectBeastActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_beast)

        recyclerView = findViewById(R.id.beastRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // 修改筛选条件：同时满足 isUnlocked 和 isCaptured 为 true
        val unlockedAndCapturedBeasts = BeastRepository.getBeasts()
            .filter { it.isUnlocked && it.isCaptured }

        val adapter = BeastSelectAdapter(unlockedAndCapturedBeasts)
        recyclerView.adapter = adapter

        // 设置返回按钮点击事件
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    inner class BeastSelectAdapter(private val beasts: List<Beast>) :
        RecyclerView.Adapter<BeastSelectAdapter.BeastViewHolder>() {

        inner class BeastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val image: ImageView = itemView.findViewById(R.id.beastImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeastViewHolder {
            val view = layoutInflater.inflate(R.layout.item_beast, parent, false)
            return BeastViewHolder(view)
        }

        override fun onBindViewHolder(holder: BeastViewHolder, position: Int) {
            val beast = beasts[position]
            holder.image.setImageResource(beast.imageResId)

            holder.image.setOnClickListener {
                // 保存选择的灵兽图片ID到SharedPreferences
                saveSelectedBeast(beast.imageResId)

                val resultIntent = Intent()
                resultIntent.putExtra("selectedBeastImage", beast.imageResId)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        override fun getItemCount(): Int = beasts.size
    }

    private fun saveSelectedBeast(imageResId: Int) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putInt("selected_beast_image", imageResId).apply()
    }
}