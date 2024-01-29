package com.flipperdevices.faphub.dao.network.ktorfit.model

import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class KtorfitBuildState {
    @SerialName("READY")
    READY,

    @SerialName("BUILD_RUNNING")
    BUILD_RUNNING,

    @SerialName("UNSUPPORTED_APPLICATION")
    UNSUPPORTED_APP,

    @SerialName("FLIPPER_OUTDATED")
    FLIPPER_OUTDATED,

    @SerialName("UNSUPPORTED_SDK")
    UNSUPPORTED_SDK;

    fun toFapBuildState(target: FlipperTarget): FapBuildState {
        if (target is FlipperTarget.Unsupported) {
            return FapBuildState.UNSUPPORTED_SDK
        }
        return when (this) {
            READY -> if (target == FlipperTarget.NotConnected) {
                FapBuildState.READY_ON_RELEASE
            } else {
                FapBuildState.READY
            }

            BUILD_RUNNING -> FapBuildState.BUILD_RUNNING
            UNSUPPORTED_APP -> FapBuildState.UNSUPPORTED_APP
            FLIPPER_OUTDATED -> FapBuildState.FLIPPER_OUTDATED
            UNSUPPORTED_SDK -> FapBuildState.UNSUPPORTED_SDK
        }
    }
}
