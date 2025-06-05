package com.daviddeer.daviddeer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.R
import android.widget.TextView
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build

class GuidePagerAdapter(private val context: Context) :
    RecyclerView.Adapter<GuidePagerAdapter.PageViewHolder>() {

    private val titles = listOf(
        "Welcome!",
        "📖 Beast Bestiary",
        "🗺️ Explore Map",
        "🎮 Play Mini-Games",
        "👣 Step Counter",
        "🐕 Choose Creature"
    )

    private val contents = listOf(
        "Unlock beasts in the Bestiary by completing mini-games.\n" +
                "• Explore the map to find and capture unlocked beasts.\n" +
                "• The Step Counter tracks your journey and calories burned.\n" +
                "• Display your favorite captured beast on the main screen.\n" +
                "• Check the Bestiary to read about the creatures you've found.",

        "Browse the beasts you’ve unlocked or captured.\n" +
                "Read their stories and learn about their mythical origins.\n" +
                "Tap on an unlocked beast to view its image.\n" +
                "To reveal its name and story, you must find and capture it through map exploration.",

        "Tap the top-right location icon to find your position.\n" +
                "Use the “Generate Beasts” button to spawn unlocked creatures nearby.\n" +
                "Move close to their location to capture them!\n" +
                "(Note: Locked beasts must be unlocked through mini-games first.)",

        "Unlock beasts by beating two fun challenges:\n" +
                "• Memory Match: Flip and match all cards to win.\n" +
                "• Reflex Test: Tap the appearing icons 10 times within 13 seconds for a perfect score.\n" +
                "You can replay games anytime!",

        "Set your daily step goal with the top-right button.\n" +
                "Track how many steps you’ve walked and how many calories you’ve burned each day.",

        "Select one of your captured beasts to proudly display on the main screen."
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.page_guide, parent, false)
        return PageViewHolder(view)
    }

    override fun getItemCount(): Int = titles.size

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.titleView.text = titles[position]
        holder.textView.text = contents[position]

        // 毛玻璃模糊效果，仅 Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurView = holder.itemView.findViewById<View>(R.id.blurBackground)
            val blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            blurView.setRenderEffect(blurEffect)
        }
    }

    class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.tvGuideTitle)
        val textView: TextView = view.findViewById(R.id.tvGuideText)
    }
}

