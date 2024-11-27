package com.flipperdevices.metric.impl.clickhouse

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.metric.api.events.complex.DebugInfoEvent
import com.flipperdevices.metric.api.events.complex.FlipperGattInfoEvent
import com.flipperdevices.metric.api.events.complex.FlipperRPCInfoEvent
import com.flipperdevices.metric.api.events.complex.RegionSource
import com.flipperdevices.metric.api.events.complex.SubGhzProvisioningEvent
import com.flipperdevices.metric.api.events.complex.SynchronizationEnd
import com.flipperdevices.metric.api.events.complex.UpdateFlipperEnd
import com.flipperdevices.metric.api.events.complex.UpdateFlipperStart
import com.flipperdevices.metric.api.events.complex.UpdateStatus
import com.flipperdevices.metric.impl.BuildConfig
import com.flipperdevices.pbmetric.Metric
import com.flipperdevices.pbmetric.events.OpenOuterClass
import com.flipperdevices.pbmetric.events.SubGhzProvisioningOuterClass
import com.flipperdevices.pbmetric.events.UpdateFlipperEndOuterClass
import com.flipperdevices.pbmetric.events.debugInfo
import com.flipperdevices.pbmetric.events.flipperGattInfo
import com.flipperdevices.pbmetric.events.flipperRpcInfo
import com.flipperdevices.pbmetric.events.open
import com.flipperdevices.pbmetric.events.subGhzProvisioning
import com.flipperdevices.pbmetric.events.synchronizationEnd
import com.flipperdevices.pbmetric.events.updateFlipperEnd
import com.flipperdevices.pbmetric.events.updateFlipperStart
import com.flipperdevices.pbmetric.metricEventsCollection
import com.flipperdevices.pbmetric.metricReportRequest
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

const val METRIC_API_URL = "https://metric.flipp.dev/report"

@Singleton
@ContributesBinding(AppGraph::class, ClickhouseApi::class)
class ClickhouseApiImpl @Inject constructor(
    private val client: HttpClient,
    private val dataStore: DataStore<Settings>,
    private val applicationParams: ApplicationParams
) : ClickhouseApi, LogTagProvider {
    override val TAG = "ClickhouseApi"

    private val scope = CoroutineScope(SupervisorJob() + FlipperDispatchers.workStealingDispatcher)
    private val sessionUUID by lazy { UUID.randomUUID() }

    @Suppress("CyclomaticComplexMethod")
    override fun reportSimpleEvent(simpleEvent: SimpleEvent, simpleEventArg: String?) {
        val openTarget = when (simpleEvent) {
            SimpleEvent.APP_OPEN -> OpenOuterClass.Open.OpenTarget.APP
            SimpleEvent.OPEN_SAVE_KEY -> OpenOuterClass.Open.OpenTarget.SAVE_KEY
            SimpleEvent.OPEN_EMULATE -> OpenOuterClass.Open.OpenTarget.EMULATE
            SimpleEvent.OPEN_EDIT -> OpenOuterClass.Open.OpenTarget.EDIT
            SimpleEvent.OPEN_SHARE -> OpenOuterClass.Open.OpenTarget.SHARE
            SimpleEvent.EXPERIMENTAL_OPEN_FM -> OpenOuterClass.Open.OpenTarget.EXPERIMENTAL_FM
            SimpleEvent.EXPERIMENTAL_OPEN_SCREEN_STREAMING ->
                OpenOuterClass.Open.OpenTarget.EXPERIMENTAL_SCREENSTREAMING

            SimpleEvent.SHARE_SHORT_LINK -> OpenOuterClass.Open.OpenTarget.SHARE_SHORTLINK
            SimpleEvent.SHARE_LONG_LINK -> OpenOuterClass.Open.OpenTarget.SHARE_LONGLINK
            SimpleEvent.SHARE_FILE -> OpenOuterClass.Open.OpenTarget.SHARE_FILE
            SimpleEvent.SAVE_DUMP -> OpenOuterClass.Open.OpenTarget.SAVE_DUMP
            SimpleEvent.MFKEY32 -> OpenOuterClass.Open.OpenTarget.MFKEY32
            SimpleEvent.OPEN_NFC_DUMP_EDITOR -> OpenOuterClass.Open.OpenTarget.OPEN_NFC_DUMP_EDITOR
            SimpleEvent.OPEN_FAPHUB -> OpenOuterClass.Open.OpenTarget.OPEN_FAPHUB
            SimpleEvent.OPEN_FAPHUB_CATEGORY -> OpenOuterClass.Open.OpenTarget.OPEN_FAPHUB_CATEGORY
            SimpleEvent.OPEN_FAPHUB_SEARCH -> OpenOuterClass.Open.OpenTarget.OPEN_FAPHUB_SEARCH
            SimpleEvent.OPEN_FAPHUB_APP -> OpenOuterClass.Open.OpenTarget.OPEN_FAPHUB_APP
            SimpleEvent.INSTALL_FAPHUB_APP -> OpenOuterClass.Open.OpenTarget.INSTALL_FAPHUB_APP
            SimpleEvent.HIDE_FAPHUB_APP -> OpenOuterClass.Open.OpenTarget.HIDE_FAPHUB_APP
            SimpleEvent.OPEN_INFRARED_LIBRARY -> OpenOuterClass.Open.OpenTarget.OPEN_INFRARED_LIBRARY
            SimpleEvent.SAVE_INFRARED_LIBRARY -> OpenOuterClass.Open.OpenTarget.SAVE_INFRARED_LIBRARY
        }

        scope.launch {
            reportToServerSafe(
                metricEventsCollection {
                    open = open {
                        target = openTarget
                        if (simpleEventArg != null) {
                            this.arg = simpleEventArg
                        }
                    }
                }
            )
        }
    }

    @Suppress("LongMethod", "ComplexMethod")
    override fun reportComplexEvent(complexEvent: ComplexEvent) {
        val event = when (complexEvent) {
            is FlipperGattInfoEvent -> metricEventsCollection {
                flipperGattInfo = flipperGattInfo {
                    flipperVersion = complexEvent.flipperVersion
                }
            }

            is FlipperRPCInfoEvent -> metricEventsCollection {
                flipperRpcInfo = flipperRpcInfo {
                    sdcardIsAvailable = complexEvent.sdCardIsAvailable
                    internalFreeByte = complexEvent.internalFreeBytes
                    internalTotalByte = complexEvent.internalTotalBytes
                    externalFreeByte = complexEvent.externalFreeBytes
                    externalTotalByte = complexEvent.externalTotalBytes
                    complexEvent.firmwareForkName?.let {
                        firmwareForkName = it
                    }
                    complexEvent.firmwareGitUrl?.let {
                        firmwareGitUrl = it
                    }
                }
            }

            is SynchronizationEnd -> metricEventsCollection {
                synchronizationEnd = synchronizationEnd {
                    subghzCount = complexEvent.subghzCount
                    rfidCount = complexEvent.rfidCount
                    nfcCount = complexEvent.nfcCount
                    infraredCount = complexEvent.infraredCount
                    ibuttonCount = complexEvent.iButtonCount
                    synchronizationTimeMs = complexEvent.synchronizationTimeMs
                    changesCount = complexEvent.changesCount
                }
            }

            is UpdateFlipperEnd -> metricEventsCollection {
                updateFlipperEnd = updateFlipperEnd {
                    updateFrom = complexEvent.updateFrom
                    updateTo = complexEvent.updateTo
                    updateId = complexEvent.updateId
                    updateStatus = when (complexEvent.updateStatus) {
                        UpdateStatus.COMPLETED ->
                            UpdateFlipperEndOuterClass.UpdateFlipperEnd.UpdateStatus.COMPLETED

                        UpdateStatus.CANCELED ->
                            UpdateFlipperEndOuterClass.UpdateFlipperEnd.UpdateStatus.CANCELED

                        UpdateStatus.FAILED_DOWNLOAD ->
                            UpdateFlipperEndOuterClass.UpdateFlipperEnd.UpdateStatus.FAILED_DOWNLOAD

                        UpdateStatus.FAILED_PREPARE ->
                            UpdateFlipperEndOuterClass.UpdateFlipperEnd.UpdateStatus.FAILED_PREPARE

                        UpdateStatus.FAILED_UPLOAD ->
                            UpdateFlipperEndOuterClass.UpdateFlipperEnd.UpdateStatus.FAILED_UPLOAD

                        UpdateStatus.FAILED ->
                            UpdateFlipperEndOuterClass.UpdateFlipperEnd.UpdateStatus.FAILED
                    }
                }
            }

            is UpdateFlipperStart -> metricEventsCollection {
                updateFlipperStart = updateFlipperStart {
                    updateFrom = complexEvent.updateFromVersion
                    updateTo = complexEvent.updateToVersion
                    updateId = complexEvent.updateId
                }
            }

            is SubGhzProvisioningEvent -> metricEventsCollection {
                subghzProvisioning = subGhzProvisioning {
                    complexEvent.regionNetwork?.let { regionNetwork = it }
                    complexEvent.regionSimOne?.let { regionSim1 = it }
                    complexEvent.regionIp?.let { regionIp = it }
                    complexEvent.regionSystem?.let { regionSystem = it }
                    complexEvent.regionProvided?.let { regionProvided = it }
                    isRoaming = complexEvent.isRoaming
                    regionSource = when (complexEvent.regionSource) {
                        RegionSource.SIM_NETWORK ->
                            SubGhzProvisioningOuterClass.SubGhzProvisioning.RegionSource.SIM_NETWORK

                        RegionSource.SIM_COUNTRY ->
                            SubGhzProvisioningOuterClass.SubGhzProvisioning.RegionSource.SIM_COUNTRY

                        RegionSource.GEO_IP ->
                            SubGhzProvisioningOuterClass.SubGhzProvisioning.RegionSource.GEO_IP

                        RegionSource.SYSTEM ->
                            SubGhzProvisioningOuterClass.SubGhzProvisioning.RegionSource.SYSTEM

                        RegionSource.DEFAULT ->
                            SubGhzProvisioningOuterClass.SubGhzProvisioning.RegionSource.DEFAULT
                    }
                }
            }

            is DebugInfoEvent -> metricEventsCollection {
                debugInfo = debugInfo {
                    key = complexEvent.key.key
                    value = complexEvent.value
                }
            }

            else -> null
        }
        if (event == null) {
            error { "Can't process event $complexEvent" }
            return
        }
        scope.launch {
            reportToServerSafe(event)
        }
    }

    private suspend fun reportToServerSafe(event: Metric.MetricEventsCollection): Unit = try {
        val reportRequest = metricReportRequest {
            uuid = getUUID()
            version = applicationParams.version
            sessionUuid = sessionUUID.toString()
            platform = if (BuildConfig.DEBUG) {
                Metric.MetricReportRequest.Platform.ANDROID_DEBUG
            } else {
                Metric.MetricReportRequest.Platform.ANDROID
            }
            events.add(event)
        }
        val httpResponse = client.post(METRIC_API_URL) {
            header(HttpHeaders.ContentType, ContentType.Application.OctetStream)
            setBody(reportRequest.toByteArray())
        }
        if (!httpResponse.status.isSuccess()) {
            error {
                "Failed report event to $METRIC_API_URL" +
                    " $reportRequest with code ${httpResponse.status}"
            }
        } else {
            verbose { "Sucs send event $event with ${reportRequest.uuid}" }
        }
    } catch (e: Exception) {
        error(e) { "Failed report to server" }
    }

    private suspend fun getUUID(): String {
        var uuid = dataStore.data.first().uuid
        if (uuid.isBlank()) {
            uuid = dataStore.updateData {
                if (it.uuid.isBlank()) {
                    it.copy(
                        uuid = UUID.randomUUID().toString()
                    )
                } else {
                    it
                }
            }.uuid
        }
        return uuid
    }
}
