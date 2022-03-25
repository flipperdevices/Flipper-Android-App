package com.flipperdevices.core.activityholder

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

object CurrentActivityHolder : Application.ActivityLifecycleCallbacks {
    private var currentActivity = WeakReference<Activity>(null)

    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun getCurrentActivity(): Activity? = currentActivity.get()

    override fun onActivityResumed(activity: Activity) {
        currentActivity = WeakReference(activity)
    }

    // Unused fun
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
