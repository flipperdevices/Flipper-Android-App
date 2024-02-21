package com.flipperdevices.updater.model

data class SubGhzProvisioningModel(
    val countries: Map<String, List<SubGhzProvisioningBand>>,
    val country: String?,
    val defaults: List<SubGhzProvisioningBand>
)

data class SubGhzProvisioningBand(
    val dutyCycle: UInt,
    val end: UInt,
    val maxPower: Int,
    val start: UInt
)
