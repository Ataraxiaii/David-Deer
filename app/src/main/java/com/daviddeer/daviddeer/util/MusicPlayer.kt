package com.daviddeer.daviddeer.util

import android.content.Context
import android.media.MediaPlayer
import com.daviddeer.daviddeer.R

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    fun start(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.bgm)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}
