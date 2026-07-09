package com.flipperdevices.bridge.connection.livetests.tests

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.LiveTestAssertionError
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.system.PingRequest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class ReconnectLiveTest @Inject constructor(
    private val orchestrator: FDeviceOrchestrator,
    private val featureProvider: FFeatureProvider
) : LiveTest {
    override val name = "Reconnect"
    override val description =
        "Disconnect and reconnect the device several times, pinging after each cycle"

    private suspend fun awaitStatus(predicate: (FDeviceConnectStatus) -> Boolean) {
        withTimeout(STATUS_TIMEOUT) {
            orchestrator.getState().first(predicate)
        }
    }

    private suspend fun awaitRpcFeature(): FRpcFeatureApi {
        return withTimeout(STATUS_TIMEOUT) {
            featureProvider.get<FRpcFeatureApi>()
                .filterIsInstance<FFeatureStatus.Supported<FRpcFeatureApi>>()
                .first()
                .featureApi
        }
    }

    private suspend fun pingOnce() {
        val response = awaitRpcFeature()
            .requestOnce(Main(system_ping_request = PingRequest()).wrapToRequest())
            .getOrThrow()
        if (response.system_ping_response == null) {
            throw LiveTestAssertionError("No ping response after reconnect: $response")
        }
    }

    override suspend fun execute(): Result<String> {
        val startStatus = orchestrator.getState().value
        if (startStatus !is FDeviceConnectStatus.Connected) {
            return Result.failure(
                LiveTestAssertionError("Expected connected device, but status is $startStatus")
            )
        }
        val device = startStatus.device
        return runCatching {
            repeat(RECONNECT_CYCLES) {
                orchestrator.disconnectCurrent()
                awaitStatus { status -> status is FDeviceConnectStatus.Disconnected }
                orchestrator.connect(device)
                awaitStatus { status -> status is FDeviceConnectStatus.Connected }
                pingOnce()
            }
            "Survived $RECONNECT_CYCLES disconnect/connect cycles with ping after each"
        }
    }

    companion object {
        private const val RECONNECT_CYCLES = 3
        private val STATUS_TIMEOUT = 30.seconds
    }
}
