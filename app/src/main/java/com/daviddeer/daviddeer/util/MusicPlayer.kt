import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    var currentResId: Int = -1
    private var pendingResId: Int = -1
    private var handler = android.os.Handler(android.os.Looper.getMainLooper())

    fun start(context: Context, resId: Int, delay: Long = 0) {
        // Log.d("MusicPlayer", "Try start: $resId, current: $currentResId, delay: $delay")

        if (mediaPlayer?.isPlaying == true && currentResId == resId) return

        // Add a small delay
        if (delay > 0) {
            pendingResId = resId
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                startImmediately(context, pendingResId)
                pendingResId = -1
            }, delay)
            return
        }

        startImmediately(context, resId)
    }

    private fun startImmediately(context: Context, resId: Int) {
        // Log.d("MusicPlayer", "StartImmediately: $resId")
        if (mediaPlayer?.isPlaying == true && currentResId == resId) return

        stop()
        try {
            currentResId = resId
            mediaPlayer = MediaPlayer.create(context.applicationContext, resId)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setOnPreparedListener {
                Log.d("MusicPlayer", "MediaPlayer prepared, starting playback")
                mediaPlayer?.start()
            }

            if (mediaPlayer != null) {
                mediaPlayer?.start()
                Log.d("MusicPlayer", "Started successfully")
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Start failed", e)
        }
    }

    fun stop() {
        Log.d("MusicPlayer", "Stop called")
        handler.removeCallbacksAndMessages(null)
        pendingResId = -1
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentResId = -1
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false
}