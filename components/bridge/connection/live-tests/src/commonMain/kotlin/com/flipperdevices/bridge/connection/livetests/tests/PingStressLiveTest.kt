package com.flipperdevices.bridge.connection.livetests.tests

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.FeatureNotSupportedError
import com.flipperdevices.bridge.connection.livetests.model.LiveTestAssertionError
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.system.PingRequest
import okio.ByteString.Companion.toByteString
import javax.inject.Inject
import kotlin.random.Random

class PingStressLiveTest @Inject constructor(
    private val featureProvider: FFeatureProvider
) : LiveTest {
    override val name = "Ping stress"
    override val description = "Send sequential pings with payload sizes from empty to 1 KiB"

    private suspend fun pingWithPayload(rpcApi: FRpcFeatureApi, payloadSize: Int) {
        val payload = Random.nextBytes(payloadSize).toByteString()
        val response = rpcApi
            .requestOnce(Main(system_ping_request = PingRequest(data_ = payload)).wrapToRequest())
            .getOrThrow()
        val echoedPayload = response.system_ping_response?.data_
            ?: throw LiveTestAssertionError("No ping payload in response for size $payloadSize")
        if (echoedPayload != payload) {
            throw LiveTestAssertionError("Ping payload mismatch for size $payloadSize")
        }
    }

    override suspend fun execute(): Result<String> {
        val rpcApi = featureProvider.getSync<FRpcFeatureApi>()
            ?: return Result.failure(FeatureNotSupportedError("RPC"))
        return runCatching {
            var totalPayloadBytes = 0
            for (payloadSize in PAYLOAD_SIZES) {
                repeat(ROUNDS_PER_SIZE) {
                    pingWithPayload(rpcApi, payloadSize)
                    totalPayloadBytes += payloadSize
                }
            }
            "Completed ${PAYLOAD_SIZES.size * ROUNDS_PER_SIZE} pings, " +
                "echoed $totalPayloadBytes payload bytes"
        }
    }

    companion object {
        private val PAYLOAD_SIZES = listOf(0, 1, 16, 256, 1024)
        private const val ROUNDS_PER_SIZE = 4
    }
}
