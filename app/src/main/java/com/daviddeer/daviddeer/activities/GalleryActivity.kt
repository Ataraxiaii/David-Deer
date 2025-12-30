package com.daviddeer.daviddeer.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.utils.ImageProcessUtils

class GalleryActivity : ComponentActivity() {

    private val TAG = "GalleryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        findViewById<android.widget.ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.galleryRecyclerView)
        val emptyView = findViewById<TextView>(R.id.emptyTextView) // 添加一个显示空状态的TextView

        // get image
        val photos = ImageProcessUtils.getAllCapturedFiles(this)
        Log.d(TAG, "Found ${photos.size} photos")

        if (photos.isEmpty()) {
            emptyView.text = "No captured photos yet"
            recyclerView.visibility = android.view.View.GONE
            emptyView.visibility = android.view.View.VISIBLE
            return
        }

        emptyView.visibility = android.view.View.GONE
        recyclerView.visibility = android.view.View.VISIBLE

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val imageView = ImageView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        400
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setPadding(8, 8, 8, 8)
                    setBackgroundColor(android.graphics.Color.LTGRAY)
                }
                return object : RecyclerView.ViewHolder(imageView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val file = photos[position]
                val imageView = holder.itemView as ImageView

                try {
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 4
                    }
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)

                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        imageView.contentDescription = "Captured photo: ${file.name}"
                    } else {
                        imageView.setImageResource(R.drawable.explorer)
                        imageView.contentDescription = "Failed to load photo"
                        Log.e(TAG, "Failed to decode bitmap for file: ${file.absolutePath}")
                    }
                } catch (e: Exception) {
                    imageView.setImageResource(R.drawable.explorer)
                    Log.e(TAG, "Error loading image: ${e.message}")
                }

                imageView.setOnClickListener {
                    showFullImageDialog(file)
                }
            }

            override fun getItemCount() = photos.size
        }


    }

    private fun showFullImageDialog(file: java.io.File) {
        val dialogView = android.view.LayoutInflater.from(this)
            .inflate(R.layout.dialog_fullscreen_photo, null)

        val imageView = dialogView.findViewById<ImageView>(R.id.fullscreenImageView)


        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.explorer)
        }

        // Extract beast name and capture time from filename
        val (beastName, captureTime) = extractInfoFromFileName(file.name)
        val title = if (captureTime.isNotEmpty()) {
            "$beastName ($captureTime)"
        } else {
            beastName
        }

        android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(title)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /**
     * Extract beast name and capture time from filename
     */
    private fun extractInfoFromFileName(fileName: String): Pair<String, String> {
        return try {
            val nameWithoutExt = fileName.substringBeforeLast(".")

            if (nameWithoutExt.startsWith("CAPTURED_")) {
                val parts = nameWithoutExt.removePrefix("CAPTURED_").split("_")
                when {
                    parts.size >= 2 -> {
                        // CAPTURED_zhuque_1700000000000
                        val beastName = parts[0]
                        val timestamp = parts[1]

                        // format timestamp
                        val formattedTime = try {
                            val ts = timestamp.toLong()
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                            sdf.format(java.util.Date(ts))
                        } catch (e: Exception) {
                            timestamp
                        }

                        Pair(beastName, formattedTime)
                    }
                    parts.size == 1 -> {
                        // CAPTURED_zhuque
                        Pair(parts[0], "")
                    }
                    else -> {
                        Pair(fileName.substringBeforeLast("."), "")
                    }
                }
            } else {
                Pair(fileName.substringBeforeLast("."), "")
            }
        } catch (e: Exception) {
            Pair(fileName.substringBeforeLast("."), "")
        }
    }
}