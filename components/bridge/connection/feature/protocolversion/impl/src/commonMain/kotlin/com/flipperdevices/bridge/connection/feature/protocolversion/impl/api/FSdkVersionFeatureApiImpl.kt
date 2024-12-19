package com.flipperdevices.bridge.connection.feature.protocolversion.impl.api

import com.flipperdevices.bridge.connection.feature.protocolversion.api.FSdkVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.property.GetRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.toList

class FSdkVersionFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
) : FSdkVersionFeatureApi {

    override suspend fun getSdkVersion(): Result<SemVer> = runCatching {
        val answers = rpcFeatureApi.request(
            Main(
                property_get_request = GetRequest(
                    key = RPC_SDK_KEY
                )
            ).wrapToRequest()
        ).toList()
        info { "Receive ${answers.size} answers by $RPC_SDK_KEY, $answers" }
        val major = answers
            .firstOrNull { result ->
                result.getOrNull()?.property_get_response?.key == RPC_SDK_MAJOR_KEY
            }
            ?.getOrNull()?.property_get_response
            ?.value_
            ?.toIntOrNull()
            ?: error("Answer doesn't contains $RPC_SDK_MAJOR_KEY")

        val minor = answers
            .firstOrNull { result ->
                result.getOrNull()?.property_get_response?.key == RPC_SDK_MINOR_KEY
            }
            ?.getOrNull()?.property_get_response
            ?.value_
            ?.toIntOrNull()
            ?: error("Answer doesn't contains $RPC_SDK_MINOR_KEY")

        info { "Receive version $major and $minor" }

        SemVer(
            majorVersion = major,
            minorVersion = minor
        )
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
        ): FSdkVersionFeatureApiImpl
    }

    companion object {
        private const val RPC_SDK_KEY = "devinfo.firmware.api"
        private const val RPC_SDK_MAJOR_KEY = "firmware.api.major"
        private const val RPC_SDK_MINOR_KEY = "firmware.api.minor"
    }
}
