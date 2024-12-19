package com.flipperdevices.bridge.connection.feature.protocolversion.impl.api

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FSdkVersionFeatureApi
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FSdkVersionFeatureApiImpl @AssistedInject constructor(
    @Assisted private val fGetInfoFeatureApi: FGetInfoFeatureApi,
) : FSdkVersionFeatureApi, LogTagProvider {
    override val TAG: String = "FSdkVersionFeatureApiImpl"

    override suspend fun getSdkVersion(): Result<SemVer> = runCatching {
        val major = fGetInfoFeatureApi.get(FGetInfoApiProperty.DeviceInfo.RPC_SDK_MAJOR_KEY)
            .onFailure { error(it) { "Answer doesn't contains ${FGetInfoApiProperty.DeviceInfo.RPC_SDK_MAJOR_KEY}" } }
            .getOrNull()
            ?.toIntOrNull()
            ?: error("Answer doesn't contains ${FGetInfoApiProperty.DeviceInfo.RPC_SDK_MAJOR_KEY}")

        val minor = fGetInfoFeatureApi.get(FGetInfoApiProperty.DeviceInfo.RPC_SDK_MINOR_KEY)
            .onFailure { error(it) { "Answer doesn't contains ${FGetInfoApiProperty.DeviceInfo.RPC_SDK_MINOR_KEY}" } }
            .getOrNull()
            ?.toIntOrNull()
            ?: error("Answer doesn't contains ${FGetInfoApiProperty.DeviceInfo.RPC_SDK_MINOR_KEY}")

        info { "Receive version $major and $minor" }

        SemVer(
            majorVersion = major,
            minorVersion = minor
        )
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            fGetInfoFeatureApi: FGetInfoFeatureApi,
        ): FSdkVersionFeatureApiImpl
    }
}
