package com.daviddeer.daviddeer.adapters

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
import android.widget.ImageView

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

        "Browse the beasts you've unlocked or captured.\n" +
                "Read their stories and learn about their mythical origins.\n" +
                "Tap on an unlocked beast to view its image.\n" +
                "To reveal its name and story, you must find and capture it through map exploration.",

        "Tap the top-right location icon to find your position.\n" +
                "Use the 'Generate Beasts' button to spawn unlocked creatures nearby.\n" +
                "Tap the map mode button to switch between standard and satellite views.\n" +
                "Move close to their location to capture them!\n" +
                "(Note: Locked beasts must be unlocked through mini-games first.)",

        "Unlock beasts by beating two fun challenges:\n" +
                "• Memory Match: Flip and match all cards to win.\n" +
                "• Reflex Test: Tap the appearing icons 10 times within 13 seconds for a perfect score.\n" +
                "You can replay games anytime!",

        "Track your daily step goal and daily activity:\n" +
                "• Tap the top-right button to set personalized step goals.\n" +
                "• Real-time step progress bar and calorie burn display.\n" +
                "• View historical step records.",

        "Here you can choose your favorite mythical beast to display on the home screen.\n" +
                "Select one captured beast as your home screen companion.\n" +
                "Your chosen beast will accompany you throughout your gaming journey."
    )

    // Image resource array
    private val images = listOf(
        R.drawable.welcome,       // Welcome page image
        R.drawable.bestiarybutton,      // Bestiary page image
        R.drawable.mapbutton,           // Map page image
        R.drawable.gamebutton,         // Game page image
        R.drawable.stepsbutton,         // Step counter page image
        R.drawable.choose_beast_button   // Creature selection page image
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.page_guide, parent, false)
        return PageViewHolder(view)
    }

    override fun getItemCount(): Int = titles.size

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.titleView.text = titles[position]
        holder.textView.text = contents[position]

        // Set image
        holder.imageView.setImageResource(images[position])

        // Apply blur effect (frosted glass effect)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurView = holder.itemView.findViewById<View>(R.id.blurBackground)
            val blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            blurView.setRenderEffect(blurEffect)
        }
    }

    class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.tvGuideTitle)
        val textView: TextView = view.findViewById(R.id.tvGuideText)
        val imageView: ImageView = view.findViewById(R.id.ivGuideImage)
    }
}