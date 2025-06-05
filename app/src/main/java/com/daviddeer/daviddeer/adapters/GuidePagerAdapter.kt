package com.daviddeer.daviddeer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.R
import android.widget.TextView

class GuidePagerAdapter(private val context: Context) :
    RecyclerView.Adapter<GuidePagerAdapter.PageViewHolder>() {

    private val pages = listOf(
        "Welcome to the world of mythic beasts!\n\nYour goal is to explore the map, play games, and unlock legendary creatures.",
        "📖 Beast Bestiary\n\nCheck which beasts you've found or unlocked, and read about their stories.",
        "🗺️ Explore Map\n\nTap the map to discover mysterious places and capture new beasts!",
        "🎮 Play Mini-Games\n\nChallenge your memory and reflexes to unlock new creatures.",
        "👣 Step Counter\n\nWalk in real life to gain energy or unlock new rewards!"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.page_guide, parent, false)
        return PageViewHolder(view)
    }

    override fun getItemCount(): Int = pages.size

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.textView.text = pages[position]
    }

    class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tvGuideText)
    }
}
