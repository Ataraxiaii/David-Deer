package com.daviddeer.daviddeer

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.adapters.BeastAdapter
import com.daviddeer.daviddeer.data.BeastRepository

//图鉴界面
class BestiaryActivity : ComponentActivity() {
    // beast 数据
    private lateinit var recyclerView: RecyclerView
    private lateinit var beastAdapter: BeastAdapter
    private val beastList = BeastRepository.getBeasts() // beast模拟数据

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bestiary) // 设置适配器

        // 图鉴列表
        recyclerView = findViewById<RecyclerView>(R.id.bestiaryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 三列网格
        beastAdapter = BeastAdapter(beastList, this)
        recyclerView.adapter = beastAdapter

        // 返回按钮
        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()  // 返回上一界面（MainActivity）
        }
    }
}
