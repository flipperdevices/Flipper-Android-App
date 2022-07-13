package com.flipperdevices.singleactivity.impl

import java.lang.ref.WeakReference

internal object SingleActivityHolder {
    private var singleActivity = WeakReference<SingleActivity?>(null)

    fun setUpSingleActivity(activity: SingleActivity) {
        singleActivity = WeakReference(activity)
    }

    fun removeSingleActivity() {
        singleActivity = WeakReference(null)
    }

    fun getSingleActivity(): SingleActivity? = singleActivity.get()
}
