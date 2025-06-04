package com.daviddeer.daviddeer.util

import android.content.Context
import android.media.MediaPlayer
import com.daviddeer.daviddeer.R

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    // 播放音乐
    fun start(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.bgm)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
    }

    // 停止播放
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}
