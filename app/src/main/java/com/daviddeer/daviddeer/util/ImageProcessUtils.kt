package com.daviddeer.daviddeer.utils

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageProcessUtils {

    private const val TAG = "ImageProcessUtils"

    /**
     * Synthesize image: background (photo) + beast
     */
    fun processAndSaveCapture(context: Context, photoPath: String, beastResId: Int, beastName: String): File? {
        try {
            Log.d(TAG, "Processing capture - photoPath: $photoPath, beast: $beastName")

            // check if photo file exists
            val photoFile = File(photoPath)
            if (!photoFile.exists()) {
                Log.e(TAG, "Photo file does not exist: $photoPath")
                return null
            }

            // load photo and make it mutable
            val options = BitmapFactory.Options().apply {
                inMutable = true
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val background = try {
                BitmapFactory.decodeFile(photoPath, options)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decode photo: ${e.message}")
                return null
            }

            if (background == null) {
                Log.e(TAG, "Background bitmap is null")
                return null
            }

            Log.d(TAG, "Background loaded: ${background.width}x${background.height}")

            // load beast image
            val beastBitmap = try {
                BitmapFactory.decodeResource(context.resources, beastResId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decode beast resource: ${e.message}")
                background.recycle()
                return null
            }

            if (beastBitmap == null) {
                Log.e(TAG, "Beast bitmap is null")
                background.recycle()
                return null
            }

            Log.d(TAG, "Beast loaded: ${beastBitmap.width}x${beastBitmap.height}")

            // create canvas
            val canvas = Canvas(background)

            // calculate beast location
            val beastWidth = (background.width * 0.4f).toInt()
            val scale = beastWidth.toFloat() / beastBitmap.width
            val beastHeight = (beastBitmap.height * scale).toInt()

            val left = (background.width - beastWidth) / 2f
            val top = background.height * 0.55f

            val destRect = RectF(left, top, left + beastWidth, top + beastHeight)

            // draw overlay
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            canvas.drawBitmap(beastBitmap, null, destRect, paint)

            // save to internal private directory
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (storageDir == null || !storageDir.exists()) {
                storageDir?.mkdirs()
                Log.d(TAG, "Created storage directory: ${storageDir?.absolutePath}")
            }

            val resultFile = File(storageDir, "CAPTURED_${beastName}_${System.currentTimeMillis()}.jpg")
            Log.d(TAG, "Saving to: ${resultFile.absolutePath}")

            try {
                FileOutputStream(resultFile).use { out ->
                    background.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    out.flush()
                }
                Log.d(TAG, "File saved successfully: ${resultFile.length()} bytes")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to save file: ${e.message}")
                background.recycle()
                beastBitmap.recycle()
                return null
            }

            // releasememory
            background.recycle()
            beastBitmap.recycle()

            // verify file exists
            if (resultFile.exists()) {
                Log.d(TAG, "Final file exists: ${resultFile.length()} bytes")
            } else {
                Log.e(TAG, "Final file does not exist!")
                return null
            }

            return resultFile
        } catch (e: Exception) {
            Log.e(TAG, "Exception in processAndSaveCapture: ${e.message}", e)
            return null
        }
    }

    /**
     * Get all synthesized photo files
     */
    fun getAllCapturedFiles(context: Context): List<File> {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d(TAG, "Looking for files in: ${storageDir?.absolutePath}")

        if (storageDir == null || !storageDir.exists()) {
            Log.d(TAG, "Storage directory does not exist")
            return emptyList()
        }

        val files = storageDir.listFiles { file ->
            val isCaptured = file.name.startsWith("CAPTURED_")
            val isJpg = file.extension.equals("jpg", ignoreCase = true) ||
                    file.extension.equals("jpeg", ignoreCase = true)
            isCaptured && isJpg
        }

        val result = files?.sortedByDescending { it.lastModified() } ?: emptyList()
        Log.d(TAG, "Found ${result.size} captured files")
        result.forEach { file ->
            Log.d(TAG, "File: ${file.name}, size: ${file.length()} bytes")
        }

        return result
    }

}