package com.daviddeer.daviddeer

import android.app.Application
import android.app.Activity
import android.os.Bundle
import com.daviddeer.daviddeer.util.MusicPlayer

class MyApp : Application(), Application.ActivityLifecycleCallbacks {
    private var activityCount = 0

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        activityCount++
        if (activityCount == 1) {
            // 从后台回到前台
            MusicPlayer.start(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
        if (activityCount == 0) {
            // 所有 Activity 都不可见，进入后台
            MusicPlayer.pause()
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= TRIM_MEMORY_COMPLETE) {
            // 系统准备回收整个 App，彻底退出
            MusicPlayer.stop()
        }
    }


    // 其他生命周期方法留空
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
