package com.flipperdevices.analytics.shake2report.impl

import android.app.Application
import android.content.Context
import android.content.Intent
import com.flipperdevices.analytics.shake2report.impl.activity.Shake2ReportActivity
import com.flipperdevices.analytics.shake2report.impl.helper.FlipperInformationMapping
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.squareup.anvil.annotations.ContributesBinding
import io.sentry.Sentry
import io.sentry.SentryEvent

@ContributesBinding(AppGraph::class)
object Shake2ReportApiImpl : Shake2ReportApi {
    internal var instance: Shake2Report? = null
        private set

    override fun init(application: Application) {
        instance = Shake2Report(application)
        instance?.register()
    }

    override fun reportBugScreen(context: Context): Screen? {
        return ActivityScreen { Intent(context, Shake2ReportActivity::class.java) }
    }

    override fun updateGattInformation(gattInformation: FlipperGATTInformation) {
        instance?.setExtra(FlipperInformationMapping.convert(gattInformation))
    }

    override fun updateRpcInformation(rpcInformation: FlipperRpcInformation) {
        instance?.setExtra(FlipperInformationMapping.convert(rpcInformation))
    }

    override fun reportException(throwable: Throwable, tag: String?, extras: Map<String, String>?) {
        val event = SentryEvent(throwable)
        extras?.let { event.setExtras(extras) }
        tag?.let { event.setTag("source", it) }

        Sentry.captureEvent(event)
    }

    internal fun initAndGet(application: Application): Shake2Report {
        val instanceInternal = Shake2Report(application)
        instanceInternal.register()
        instance = instanceInternal
        return instanceInternal
    }
}
