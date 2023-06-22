package com.flipperdevices.faphub.dao.network.retrofit.model

import com.flipperdevices.faphub.dao.api.model.FapBuildState
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

    fun toFapBuildState(): FapBuildState = when (this) {
        READY -> FapBuildState.READY
        BUILD_RUNNING -> FapBuildState.BUILD_RUNNING
        UNSUPPORTED_APP -> FapBuildState.UNSUPPORTED_APP
        FLIPPER_OUTDATED -> FapBuildState.FLIPPER_OUTDATED
        UNSUPPORTED_SDK -> FapBuildState.UNSUPPORTED_SDK
    }
}
