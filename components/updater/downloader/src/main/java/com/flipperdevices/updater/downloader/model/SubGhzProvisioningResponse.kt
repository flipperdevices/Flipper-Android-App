package com.flipperdevices.updater.downloader.model

import com.flipperdevices.updater.model.SubGhzProvisioningBand
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SubGhzProvisioningResponse(
    @SerialName("success")
    val success: SubGhzProvisioningSuccessfulResponse? = null,
    @SerialName("error")
    val error: SubGhzProvisioningErrorResponse? = null
)

@Serializable
internal data class SubGhzProvisioningSuccessfulResponse(
    @SerialName("bands")
    val bands: Map<String, Band>,
    @SerialName("countries")
    val countriesBands: Map<String, List<String>>,
    @SerialName("country")
    val countryCode: String,
    @SerialName("default")
    val defaultBands: List<String>
)

@Serializable
internal data class Band(
    @SerialName("duty_cycle")
    val dutyCycle: UInt,
    @SerialName("end")
    val end: UInt,
    @SerialName("max_power")
    val maxPower: Int,
    @SerialName("start")
    val start: UInt
) {
    fun toSubGhzProvisioningBand(): SubGhzProvisioningBand {
        return SubGhzProvisioningBand(
            dutyCycle = dutyCycle,
            end = end,
            maxPower = maxPower,
            start = start
        )
    }
}

@Serializable
internal data class SubGhzProvisioningErrorResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("text")
    val text: String
)
