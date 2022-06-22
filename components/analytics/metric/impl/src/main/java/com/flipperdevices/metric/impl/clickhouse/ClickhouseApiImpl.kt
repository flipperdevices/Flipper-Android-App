package com.flipperdevices.metric.impl.clickhouse

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.metric.api.events.complex.FlipperGattInfoEvent
import com.flipperdevices.metric.api.events.complex.FlipperRPCInfoEvent
import com.flipperdevices.metric.api.events.complex.SynchronizationEnd
import com.flipperdevices.metric.api.events.complex.UpdateFlipperEnd
import com.flipperdevices.metric.api.events.complex.UpdateFlipperStart
import com.flipperdevices.metric.api.events.complex.UpdateStatus
import com.flipperdevices.metric.impl.BuildConfig
import com.flipperdevices.pbmetric.Metric
import com.flipperdevices.pbmetric.events.OpenOuterClass
import com.flipperdevices.pbmetric.events.UpdateFlipperEndOuterClass
import com.flipperdevices.pbmetric.events.flipperGattInfo
import com.flipperdevices.pbmetric.events.flipperRpcInfo
import com.flipperdevices.pbmetric.events.open
import com.flipperdevices.pbmetric.events.synchronizationEnd
import com.flipperdevices.pbmetric.events.updateFlipperEnd
import com.flipperdevices.pbmetric.events.updateFlipperStart
import com.flipperdevices.pbmetric.metricEventsCollection
import com.flipperdevices.pbmetric.metricReportRequest
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

const val METRIC_API_URL = ""

@ContributesBinding(AppGraph::class, ClickhouseApi::class)
class ClickhouseApiImpl @Inject constructor(
    private val client: HttpClient,
    private val dataStore: DataStore<Settings>
) : ClickhouseApi, LogTagProvider {
    override val TAG = "ClickhouseApi"

    private val scope = CoroutineScope(SupervisorJob())

    override fun reportSimpleEvent(simpleEvent: SimpleEvent) {
        val openTarget = when (simpleEvent) {
            SimpleEvent.APP_OPEN -> OpenOuterClass.Open.OpenTarget.APP
            SimpleEvent.OPEN_SAVE_KEY -> OpenOuterClass.Open.OpenTarget.SAVE_KEY
            SimpleEvent.OPEN_EMULATE -> OpenOuterClass.Open.OpenTarget.EMULATE
            SimpleEvent.OPEN_EDIT -> OpenOuterClass.Open.OpenTarget.EDIT
            SimpleEvent.OPEN_SHARE -> OpenOuterClass.Open.OpenTarget.SHARE
            SimpleEvent.EXPERIMENTAL_OPEN_FM -> OpenOuterClass.Open.OpenTarget.EXPERIMENTAL_FM
            SimpleEvent.EXPERIMENTAL_OPEN_SCREENSTREAMING ->
                OpenOuterClass.Open.OpenTarget.EXPERIMENTAL_SCREENSTREAMING
        }
        scope.launch(Dispatchers.Default) {
            reportToServerSafe(
                metricEventsCollection {
                    open = open {
                        target = openTarget
                    }
                }
            )
        }
    }

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
            else -> null
        }
        if (event == null) {
            error { "Can't process event $complexEvent" }
            return
        }
        scope.launch(Dispatchers.Default) {
            reportToServerSafe(event)
        }
    }

    private suspend fun reportToServerSafe(event: Metric.MetricEventsCollection): Unit = try {
        val reportRequestBytes = metricReportRequest {
            uuid = getUUID()
            platform = if (BuildConfig.INTERNAL) {
                Metric.MetricReportRequest.Platform.ANDROID_DEBUG
            } else Metric.MetricReportRequest.Platform.ANDROID
            events.add(event)
        }.toByteArray()
        val httpResponse = client.post(METRIC_API_URL) {
            setBody(reportRequestBytes)
        }
        if (!httpResponse.status.isSuccess()) {
            error { "Failed report event to $METRIC_API_URL $event" }
        } else Unit
    } catch (e: Exception) {
        error(e) { "Failed report to server" }
    }

    private suspend fun getUUID(): String {
        var uuid = dataStore.data.first().uuid
        if (uuid.isNullOrBlank()) {
            uuid = dataStore.updateData {
                if (it.uuid.isNullOrBlank()) {
                    it.toBuilder()
                        .setUuid(UUID.randomUUID().toString())
                        .build()
                } else it
            }.uuid
        }
        return uuid
    }
}
