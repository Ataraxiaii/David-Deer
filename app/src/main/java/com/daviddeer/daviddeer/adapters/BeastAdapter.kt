package com.daviddeer.daviddeer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.data.Beast
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton

class BeastAdapter(
    private val beasts: List<Beast>,
    private val context: Context
) : RecyclerView.Adapter<BeastAdapter.BeastViewHolder>() {

    inner class BeastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.beastImage)
        val name: TextView = view.findViewById(R.id.beastName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beast, parent, false)
        return BeastViewHolder(view)
    }

    // 图鉴展示
    override fun onBindViewHolder(holder: BeastViewHolder, position: Int) {
        val beast = beasts[position]

        // 是否游戏闯关，关乎图片是否展示
        if (!beast.isUnlocked) {
            holder.image.setImageResource(R.drawable.beastlocked) // 没有通过闯关，黑图
            holder.name.visibility = View.INVISIBLE // 姓名不可见
        } else {
            holder.image.setImageResource(beast.imageResId) // 游戏闯关通过可以看见图
            holder.name.visibility = View.VISIBLE
            holder.name.text = if (beast.isCaptured) beast.name else "???" // 地图捕捉之后故事可见，否则不可见
        }

        // 游戏通关与未通关的点击事件
        holder.itemView.setOnClickListener {
            if (!beast.isUnlocked) {
                // 显示未解锁弹窗
                showUnlockedDialog()
            } else {
                // 通过
                showBeastDialog(beast, beast.isCaptured)
            }
        }
    }

    // 显示未解锁弹窗的方法
    private fun showUnlockedDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_beast_unlocked, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes.gravity = Gravity.CENTER
        }

        dialogView.findViewById<Button>(R.id.btnOK).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount(): Int = beasts.size

    // 详细灵兽图鉴展示
    private fun showBeastDialog(beast: Beast, showDetails: Boolean) {
        try {
            Log.d("BeastAdapter", "Showing dialog for beast: ${beast.name}, showDetails: $showDetails")
            val builder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_beast_detail, null)

            val img = view.findViewById<ImageView>(R.id.dialogBeastImage)
            val name = view.findViewById<TextView>(R.id.dialogBeastName)
            val story = view.findViewById<TextView>(R.id.dialogBeastStory)
            val btnClose = view.findViewById<ImageButton>(R.id.btnClose)

            img.setImageResource(beast.imageResId)

            // 具体灵兽信息 捕捉后展示名字，故事，否则不展示
            if (showDetails) {
                name.text = beast.name
                story.text = beast.story
            } else {
                name.text = "???"
                story.text = "The beast has not yet been captured."
            }
//
//            builder.setView(view)
//                .setPositiveButton("Close", null)
//                .create().show()

            val dialog = builder.setView(view).create()

            // 点击叉叉关闭对话框
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        } catch (e: Exception) {
            Log.e("BeastAdapter", "Error showing beast dialog: ${e.message}", e)
            Toast.makeText(context, "An error occurred while showing the beast details.", Toast.LENGTH_SHORT).show()
        }
    }
}