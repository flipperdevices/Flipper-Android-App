package com.flipperdevices.analytics.shake2report.impl.helper

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import java.lang.ref.WeakReference

class ScreenshotHelper(
    private val application: Application
) : Application.ActivityLifecycleCallbacks {
    private var currentActivity = WeakReference<Activity>(null)

    fun register() {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun takeScreenshot(): Bitmap? {
        val rootView = currentActivity.get()?.window?.decorView?.rootView ?: return null
        val width = rootView.width
        val height = rootView.height
        val screenshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        rootView.draw(Canvas(screenshot))
        return screenshot
    }

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
