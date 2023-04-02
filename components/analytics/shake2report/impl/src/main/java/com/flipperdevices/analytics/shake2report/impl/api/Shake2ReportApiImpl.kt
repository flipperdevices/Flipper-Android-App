package com.flipperdevices.analytics.shake2report.impl.api

import com.flipperdevices.analytics.shake2report.impl.InternalShake2Report
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesBinding
import io.sentry.Sentry
import io.sentry.SentryEvent
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class Shake2ReportApiImpl @Inject constructor(
    private val internalShake2Report: InternalShake2Report
) : Shake2ReportApi {
    override fun init() {
        internalShake2Report.register()
    }

    override fun updateGattInformation(gattInformation: FlipperGATTInformation) {
        internalShake2Report.setExtra(convert(gattInformation))
    }

    override fun setExtra(tags: List<Pair<String, String>>) {
        internalShake2Report.setExtra(tags)
    }

    override fun reportException(throwable: Throwable, tag: String?, extras: Map<String, String>?) {
        val event = SentryEvent(throwable)
        extras?.let { event.setExtras(extras) }
        tag?.let { event.setTag("source", it) }

        Sentry.captureEvent(event)
    }

    override fun isInitialized() = internalShake2Report.getIsRegisteredFlow()
}

private const val FLIPPER_DEVICE_NAME = "flipper_device_name"
private const val FLIPPER_MANUFACTURER_NAME = "flipper_manufacturer_name"
private const val FLIPPER_HARDWARE_REVISION = "flipper_hardware_revision"
private const val FLIPPER_SOFTWARE_REVISION = "flipper_software_revision"
private const val FLIPPER_BATTERY_LEVEL = "flipper_battery_level"
private fun convert(gattInformation: FlipperGATTInformation): List<Pair<String, String>> {
    val tagsList = mutableListOf<Pair<String, String>>()
    gattInformation.deviceName?.let {
        tagsList.add(FLIPPER_DEVICE_NAME to it)
    }
    gattInformation.manufacturerName?.let {
        tagsList.add(FLIPPER_MANUFACTURER_NAME to it)
    }
    gattInformation.hardwareRevision?.let {
        tagsList.add(FLIPPER_HARDWARE_REVISION to it)
    }
    gattInformation.softwareVersion?.let {
        tagsList.add(FLIPPER_SOFTWARE_REVISION to it)
    }
    gattInformation.batteryLevel?.let {
        tagsList.add(FLIPPER_BATTERY_LEVEL to it.toString())
    }
    return tagsList
}
