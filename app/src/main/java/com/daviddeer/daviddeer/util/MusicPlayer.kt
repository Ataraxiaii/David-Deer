package com.daviddeer.daviddeer.util

import android.content.Context
import android.media.MediaPlayer
import com.daviddeer.daviddeer.R

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var isPaused = false // 当app处于后台是暂停bgm

    // 播放音乐
    fun start(context: Context) {
        if (mediaPlayer == null) {
            // 播放
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.bgm)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else if (isPaused) {
            // 暂停
            mediaPlayer?.start()
            isPaused = false
        }
    }

    // 暂停音乐
    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPaused = true
        }
    }

    // 恢复音乐
    fun resume() {
        if (isPaused) {
            mediaPlayer?.start()
            isPaused = false
        }
    }

    // 停止播放
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPaused = false
    }

}
