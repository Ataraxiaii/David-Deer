package com.daviddeer.daviddeer

import android.app.Application
import android.app.Activity
import android.os.Bundle


class MyApp : Application(), Application.ActivityLifecycleCallbacks {
    private var activityCount = 0

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        activityCount++
        if (activityCount == 1) {
            // Returned from background to foreground
            MusicPlayer.start(activity, R.raw.bgm)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
        if (activityCount == 0) {
            MusicPlayer.stop()
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= TRIM_MEMORY_COMPLETE) {
            // System is preparing to reclaim the entire app, fully exit
            MusicPlayer.stop()
        }
    }


    // Other lifecycle methods left empty
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
