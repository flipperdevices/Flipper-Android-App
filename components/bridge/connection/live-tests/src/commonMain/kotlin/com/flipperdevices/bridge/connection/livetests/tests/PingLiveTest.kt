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
import dev.zacsweers.metro.Inject
import kotlin.random.Random

class PingLiveTest @Inject constructor(
    private val featureProvider: FFeatureProvider
) : LiveTest {
    override val name = "Ping"
    override val description = "Send system ping and verify the payload is echoed back"

    override suspend fun execute(): Result<String> {
        val rpcApi = featureProvider.getSync<FRpcFeatureApi>()
            ?: return Result.failure(FeatureNotSupportedError("RPC"))
        val payload = Random.nextBytes(PING_PAYLOAD_SIZE_BYTES).toByteString()
        val pingRequest = Main(system_ping_request = PingRequest(data_ = payload))
        return rpcApi.requestOnce(pingRequest.wrapToRequest()).mapCatching { response ->
            val echoedPayload = response.system_ping_response?.data_
                ?: throw LiveTestAssertionError("Response has no ping payload: $response")
            if (echoedPayload != payload) {
                throw LiveTestAssertionError(
                    "Ping payload mismatch: sent ${payload.hex()}, received ${echoedPayload.hex()}"
                )
            }
            "Flipper echoed ${echoedPayload.size} bytes back"
        }
    }

    companion object {
        private const val PING_PAYLOAD_SIZE_BYTES = 16
    }
}
