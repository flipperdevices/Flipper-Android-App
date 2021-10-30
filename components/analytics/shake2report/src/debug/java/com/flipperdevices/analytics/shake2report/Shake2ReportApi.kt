package com.flipperdevices.analytics.shake2report

import android.app.Application

object Shake2ReportApi {
    internal var instance: Shake2Report? = null
        private set

    fun init(application: Application) {
        instance = Shake2Report(application)
        instance?.register()
    }

    internal fun initAndGet(application: Application): Shake2Report {
        val instanceInternal = Shake2Report(application)
        instanceInternal.register()
        instance = instanceInternal
        return instanceInternal
    }
}
