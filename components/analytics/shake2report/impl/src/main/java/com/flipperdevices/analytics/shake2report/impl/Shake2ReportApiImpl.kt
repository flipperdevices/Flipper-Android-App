package com.flipperdevices.analytics.shake2report.impl

import android.app.Application
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesBinding

@ContributesBinding(AppGraph::class)
object Shake2ReportApiImpl : Shake2ReportApi {
    internal var instance: Shake2Report? = null
        private set

    override fun init(application: Application) {
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
