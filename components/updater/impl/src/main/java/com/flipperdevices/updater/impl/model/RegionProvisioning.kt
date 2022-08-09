package com.flipperdevices.updater.impl.model

data class RegionProvisioning(
    val region: String,
    val source: RegionProvisioningSource
)

enum class RegionProvisioningSource {
    SIM_NETWORK,
    SIM_COUNTRY,
    GEO_IP,
    SYSTEM
}
