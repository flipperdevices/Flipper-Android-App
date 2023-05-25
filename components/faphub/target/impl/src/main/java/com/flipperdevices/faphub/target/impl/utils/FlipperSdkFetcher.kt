package com.flipperdevices.faphub.target.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.target.impl.model.FlipperSdkVersion
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

private val RPC_SUPPORTED_VERSION =
    SemVer(majorVersion = 0, minorVersion = 16) // TODO migrate to 17
private const val RPC_SDK_MAJOR_KEY = "firmware.api.major"
private const val RPC_SDK_MINOR_KEY = "firmware.api.minor"

class FlipperSdkFetcher @Inject constructor() {
    fun getSdkApiFlow(
        requestApi: FlipperRequestApi,
        currentVersion: SemVer?
    ): Flow<FlipperSdkVersion> {
        if (currentVersion == null) {
            info { "Don't receive version, skip" }
            return flowOf(FlipperSdkVersion.InProgress)
        } else if (currentVersion < RPC_SUPPORTED_VERSION) {
            info { "Current version RPC is outdated ($currentVersion). Need $RPC_SUPPORTED_VERSION" }
            return flowOf(FlipperSdkVersion.Unsupported)
        }

        return combine(
            requestApi.request(
                main {
                    propertyGetRequest = getRequest {
                        key = RPC_SDK_MAJOR_KEY
                    }
                }.wrapToRequest()
            ),
            requestApi.request(
                main {
                    propertyGetRequest = getRequest {
                        key = RPC_SDK_MINOR_KEY
                    }
                }.wrapToRequest()
            )
        ) { majorResponse, minorResponse ->
            info { "Receive $majorResponse and $minorResponse" }
            val major = if (majorResponse.hasPropertyGetResponse()) {
                majorResponse.propertyGetResponse.value.toIntOrNull()
                    ?: return@combine FlipperSdkVersion.Error
            } else {
                return@combine FlipperSdkVersion.Error
            }
            val minor = if (minorResponse.hasPropertyGetResponse()) {
                minorResponse.propertyGetResponse.value.toIntOrNull()
                    ?: return@combine FlipperSdkVersion.Error
            } else {
                return@combine FlipperSdkVersion.Error
            }
            FlipperSdkVersion.Received(SemVer(majorVersion = major, minorVersion = minor))
        }
    }
}
