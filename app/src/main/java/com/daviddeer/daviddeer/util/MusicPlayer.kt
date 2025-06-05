package com.daviddeer.daviddeer.util

import android.content.Context
import android.media.MediaPlayer
import com.daviddeer.daviddeer.R

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var isPaused = false // Pause BGM when the app is in the background

    // Start playing music
    fun start(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.bgm)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else if (isPaused) {
            mediaPlayer?.start()
            isPaused = false
        }
    }

    // Pause music
    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPaused = true
        }
    }

    // Resume music
    fun resume() {
        if (isPaused) {
            mediaPlayer?.start()
            isPaused = false
        }
    }

    // Stop music playback
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPaused = false
    }
}