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

    // Beast encyclopedia display
    override fun onBindViewHolder(holder: BeastViewHolder, position: Int) {
        val beast = beasts[position]

        // Check whether the game level has been passed, determines whether the image is shown
        if (!beast.isUnlocked) {
            holder.image.setImageResource(R.drawable.beastlocked) // Not unlocked, show silhouette image
            holder.name.visibility = View.INVISIBLE // Name is not visible
        } else {
            holder.image.setImageResource(beast.imageResId) // Unlocked, show actual image
            holder.name.visibility = View.VISIBLE
            holder.name.text = if (beast.isCaptured) beast.name else "???" // Name visible only after capture
        }

        // Click events for both locked and unlocked beasts
        holder.itemView.setOnClickListener {
            if (!beast.isUnlocked) {
                // Show dialog for not yet unlocked
                showUnlockedDialog()
            } else {
                // Unlocked
                showBeastDialog(beast, beast.isCaptured)
            }
        }
    }

    // Method to show dialog when not yet unlocked
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

    // Detailed beast info dialog display
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

            // Display specific beast info only after capture
            if (showDetails) {
                name.text = beast.name
                story.text = beast.story
            } else {
                name.text = "???"
                story.text = "The beast has not yet been captured. Please go to the map view to capture it!"
            }
//
//            builder.setView(view)
//                .setPositiveButton("Close", null)
//                .create().show()

            val dialog = builder.setView(view).create()

            // Click the close button to dismiss the dialog
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