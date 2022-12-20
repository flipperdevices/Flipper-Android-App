package com.flipperdevices.updater.subghz.helpers.model

private const val COUNTRY_CODE_LENGTH = 2

data class RegionProvisioning(
    val regionFromNetwork: String?,
    val regionFromSim: String?,
    val regionFromIp: String?,
    val regionSystem: String?,
    val isRoaming: Boolean
) {
    fun provideRegion(): Pair<String?, RegionProvisioningSource?> {
        return if (regionFromNetwork.isOkayRegion()) {
            regionFromNetwork to RegionProvisioningSource.SIM_NETWORK
        } else if (regionFromSim.isOkayRegion() && !isRoaming) {
            regionFromSim to RegionProvisioningSource.SIM_COUNTRY
        } else if (regionFromIp.isOkayRegion()) {
            regionFromIp to RegionProvisioningSource.GEO_IP
        } else if (regionSystem.isOkayRegion()) {
            regionSystem to RegionProvisioningSource.SYSTEM
        } else {
            null to null
        }
    }
}

private fun String?.isOkayRegion(): Boolean =
    this != null && isNotBlank() && length == COUNTRY_CODE_LENGTH

enum class RegionProvisioningSource {
    SIM_NETWORK,
    SIM_COUNTRY,
    GEO_IP,
    SYSTEM,
    DEFAULT
}
