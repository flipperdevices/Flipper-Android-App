package com.flipperdevices.faphub.target.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.constants.FapHubConstants
import com.flipperdevices.faphub.target.impl.model.FlipperSdkVersion
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getRequest
import javax.inject.Inject
import kotlinx.coroutines.flow.toList

private const val RPC_SDK_KEY = "devinfo.firmware.api"
private const val RPC_SDK_MAJOR_KEY = "firmware.api.major"
private const val RPC_SDK_MINOR_KEY = "firmware.api.minor"

class FlipperSdkFetcher @Inject constructor() : LogTagProvider {
    override val TAG = "FlipperSdkFetcher"
    suspend fun getSdkApi(
        requestApi: FlipperRequestApi,
        currentVersion: SemVer?
    ): FlipperSdkVersion {
        if (currentVersion == null) {
            info { "Don't receive version, skip" }
            return FlipperSdkVersion.InProgress
        } else if (currentVersion < FapHubConstants.RPC_SUPPORTED_VERSION) {
            info {
                "Current version RPC is outdated ($currentVersion). " +
                        "Need ${FapHubConstants.RPC_SUPPORTED_VERSION}"
            }
            return FlipperSdkVersion.Unsupported
        }

        val answers = requestApi.request(
            main {
                propertyGetRequest = getRequest {
                    key = RPC_SDK_KEY
                }
            }.wrapToRequest()
        ).toList()
        info { "Receive ${answers.size} answers by $RPC_SDK_KEY, $answers" }

        val major = answers.find {
            it.hasPropertyGetResponse() &&
                    it.propertyGetResponse.key == RPC_SDK_MAJOR_KEY
        }?.propertyGetResponse?.value?.toIntOrNull() ?: return FlipperSdkVersion.Error
        val minor = answers.find {
            it.hasPropertyGetResponse() &&
                    it.propertyGetResponse.key == RPC_SDK_MINOR_KEY
        }?.propertyGetResponse?.value?.toIntOrNull() ?: return FlipperSdkVersion.Error

        info { "Receive version $major and $minor" }

        return FlipperSdkVersion.Received(
            SemVer(
                majorVersion = major,
                minorVersion = minor
            )
        )
    }
}
