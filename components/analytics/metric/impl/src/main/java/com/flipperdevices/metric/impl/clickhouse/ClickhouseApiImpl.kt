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
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.pbmetric.MetricEventsCollection
import com.flipperdevices.pbmetric.MetricReportRequest
import com.flipperdevices.pbmetric.MetricReportRequest.Platform
import com.flipperdevices.pbmetric.events.DebugInfo as DebugInfoProto
import com.flipperdevices.pbmetric.events.FlipperGattInfo as FlipperGattInfoProto
import com.flipperdevices.pbmetric.events.FlipperRpcInfo as FlipperRpcInfoProto
import com.flipperdevices.pbmetric.events.Open
import com.flipperdevices.pbmetric.events.Open.OpenTarget
import com.flipperdevices.pbmetric.events.SubGhzProvisioning as SubGhzProvisioningProto
import com.flipperdevices.pbmetric.events.SubGhzProvisioning.RegionSource as RegionSourceProto
import com.flipperdevices.pbmetric.events.SynchronizationEnd as SynchronizationEndProto
import com.flipperdevices.pbmetric.events.UpdateFlipperEnd as UpdateFlipperEndProto
import com.flipperdevices.pbmetric.events.UpdateFlipperEnd.UpdateStatus as UpdateStatusProto
import com.flipperdevices.pbmetric.events.UpdateFlipperStart as UpdateFlipperStartProto
import dev.zacsweers.metro.ContributesBinding
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
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

const val METRIC_API_URL = "https://metric.flipp.dev/report"

@SingleIn(AppGraph::class)
@ContributesBinding(AppGraph::class, binding<ClickhouseApi>())
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
            SimpleEvent.APP_OPEN -> OpenTarget.APP
            SimpleEvent.OPEN_SAVE_KEY -> OpenTarget.SAVE_KEY
            SimpleEvent.OPEN_EMULATE -> OpenTarget.EMULATE
            SimpleEvent.OPEN_EDIT -> OpenTarget.EDIT
            SimpleEvent.OPEN_SHARE -> OpenTarget.SHARE
            SimpleEvent.EXPERIMENTAL_OPEN_FM -> OpenTarget.EXPERIMENTAL_FM
            SimpleEvent.EXPERIMENTAL_OPEN_SCREEN_STREAMING -> OpenTarget.EXPERIMENTAL_SCREENSTREAMING

            SimpleEvent.SHARE_SHORT_LINK -> OpenTarget.SHARE_SHORTLINK
            SimpleEvent.SHARE_LONG_LINK -> OpenTarget.SHARE_LONGLINK
            SimpleEvent.SHARE_FILE -> OpenTarget.SHARE_FILE
            SimpleEvent.SAVE_DUMP -> OpenTarget.SAVE_DUMP
            SimpleEvent.MFKEY32 -> OpenTarget.MFKEY32
            SimpleEvent.OPEN_NFC_DUMP_EDITOR -> OpenTarget.OPEN_NFC_DUMP_EDITOR
            SimpleEvent.OPEN_FAPHUB -> OpenTarget.OPEN_FAPHUB
            SimpleEvent.OPEN_FAPHUB_CATEGORY -> OpenTarget.OPEN_FAPHUB_CATEGORY
            SimpleEvent.OPEN_FAPHUB_SEARCH -> OpenTarget.OPEN_FAPHUB_SEARCH
            SimpleEvent.OPEN_FAPHUB_APP -> OpenTarget.OPEN_FAPHUB_APP
            SimpleEvent.INSTALL_FAPHUB_APP -> OpenTarget.INSTALL_FAPHUB_APP
            SimpleEvent.HIDE_FAPHUB_APP -> OpenTarget.HIDE_FAPHUB_APP
            SimpleEvent.OPEN_INFRARED_LIBRARY -> OpenTarget.OPEN_INFRARED_LIBRARY
            SimpleEvent.SAVE_INFRARED_LIBRARY -> OpenTarget.SAVE_INFRARED_LIBRARY
        }

        scope.launch {
            reportToServerSafe(
                MetricEventsCollection(
                    open_ = Open(
                        target = openTarget,
                        arg = simpleEventArg.orEmpty()
                    )
                )
            )
        }
    }

    @Suppress("LongMethod", "ComplexMethod", "CyclomaticComplexMethod")
    override fun reportComplexEvent(complexEvent: ComplexEvent) {
        val event = when (complexEvent) {
            is FlipperGattInfoEvent -> MetricEventsCollection(
                flipper_gatt_info = FlipperGattInfoProto(
                    flipper_version = complexEvent.flipperVersion
                )
            )

            is FlipperRPCInfoEvent -> MetricEventsCollection(
                flipper_rpc_info = FlipperRpcInfoProto(
                    sdcard_is_available = complexEvent.sdCardIsAvailable,
                    internal_free_byte = complexEvent.internalFreeBytes,
                    internal_total_byte = complexEvent.internalTotalBytes,
                    external_free_byte = complexEvent.externalFreeBytes,
                    external_total_byte = complexEvent.externalTotalBytes,
                    firmware_fork_name = complexEvent.firmwareForkName.orEmpty(),
                    firmware_git_url = complexEvent.firmwareGitUrl.orEmpty()
                )
            )

            is SynchronizationEnd -> MetricEventsCollection(
                synchronization_end = SynchronizationEndProto(
                    subghz_count = complexEvent.subghzCount,
                    rfid_count = complexEvent.rfidCount,
                    nfc_count = complexEvent.nfcCount,
                    infrared_count = complexEvent.infraredCount,
                    ibutton_count = complexEvent.iButtonCount,
                    synchronization_time_ms = complexEvent.synchronizationTimeMs,
                    changes_count = complexEvent.changesCount
                )
            )

            is UpdateFlipperEnd -> MetricEventsCollection(
                update_flipper_end = UpdateFlipperEndProto(
                    update_from = complexEvent.updateFrom,
                    update_to = complexEvent.updateTo,
                    update_id = complexEvent.updateId,
                    update_status = when (complexEvent.updateStatus) {
                        UpdateStatus.COMPLETED -> UpdateStatusProto.COMPLETED
                        UpdateStatus.CANCELED -> UpdateStatusProto.CANCELED
                        UpdateStatus.FAILED_DOWNLOAD -> UpdateStatusProto.FAILED_DOWNLOAD
                        UpdateStatus.FAILED_PREPARE -> UpdateStatusProto.FAILED_PREPARE
                        UpdateStatus.FAILED_UPLOAD -> UpdateStatusProto.FAILED_UPLOAD
                        UpdateStatus.FAILED -> UpdateStatusProto.FAILED
                    }
                )
            )

            is UpdateFlipperStart -> MetricEventsCollection(
                update_flipper_start = UpdateFlipperStartProto(
                    update_from = complexEvent.updateFromVersion,
                    update_to = complexEvent.updateToVersion,
                    update_id = complexEvent.updateId
                )
            )

            is SubGhzProvisioningEvent -> MetricEventsCollection(
                subghz_provisioning = SubGhzProvisioningProto(
                    region_network = complexEvent.regionNetwork.orEmpty(),
                    region_sim_1 = complexEvent.regionSimOne.orEmpty(),
                    region_ip = complexEvent.regionIp.orEmpty(),
                    region_system = complexEvent.regionSystem.orEmpty(),
                    region_provided = complexEvent.regionProvided.orEmpty(),
                    is_roaming = complexEvent.isRoaming,
                    region_source = when (complexEvent.regionSource) {
                        RegionSource.SIM_NETWORK -> RegionSourceProto.SIM_NETWORK
                        RegionSource.SIM_COUNTRY -> RegionSourceProto.SIM_COUNTRY
                        RegionSource.GEO_IP -> RegionSourceProto.GEO_IP
                        RegionSource.SYSTEM -> RegionSourceProto.SYSTEM
                        RegionSource.DEFAULT -> RegionSourceProto.DEFAULT
                    }
                )
            )

            is DebugInfoEvent -> MetricEventsCollection(
                debug_info = DebugInfoProto(
                    key = complexEvent.key.key,
                    value_ = complexEvent.value
                )
            )

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

    private suspend fun reportToServerSafe(event: MetricEventsCollection): Unit = try {
        val reportRequest = MetricReportRequest(
            uuid = getUUID(),
            version = applicationParams.version,
            session_uuid = sessionUUID.toString(),
            platform = if (BuildKonfig.CRASH_APP_ON_FAILED_CHECKS) {
                Platform.ANDROID_DEBUG
            } else {
                Platform.ANDROID
            },
            events = listOf(event)
        )
        val httpResponse = client.post(METRIC_API_URL) {
            header(HttpHeaders.ContentType, ContentType.Application.OctetStream)
            setBody(MetricReportRequest.ADAPTER.encode(reportRequest))
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
