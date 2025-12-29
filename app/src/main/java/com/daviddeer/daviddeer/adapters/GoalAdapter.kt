package com.daviddeer.daviddeer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.R

class GoalAdapter(
    private var list: List<Pair<String, Int>>,
    private val onClick: (Pair<String, Int>) -> Unit,
    private val onLongClick: (Pair<String, Int>) -> Unit
) : RecyclerView.Adapter<GoalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvSteps: TextView = view.findViewById(R.id.tvItemSteps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvName.text = item.first
        holder.tvSteps.text = "${item.second} steps"

        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    override fun getItemCount() = list.size

    // refresh data
    fun updateData(newList: List<Pair<String, Int>>) {
        this.list = newList
        notifyDataSetChanged()
    }
}