package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignalRequestModel(
    @SerialName("success_results")
    val successResults: List<SignalResultData> = emptyList(),
    @SerialName("failed_results")
    val failedResults: List<SignalResultData> = emptyList(),
    @SerialName("skipped_results")
    val skippedResults: List<SignalResultData> = emptyList(),
    @SerialName("brand_id")
    val brandId: Long
) {
    @Serializable
    data class SignalResultData(
        @SerialName("signal_id")
        val signalId: Long,
    )
}
