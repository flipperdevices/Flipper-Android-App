package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class SubGhzProvisioningEvent(
    val regionNetwork: String?,
    val regionSimOne: String?,
    val regionIp: String?,
    val regionSystem: String?,
    val regionProvided: String?,
    val regionSource: RegionSource,
    val isRoaming: Boolean
) : ComplexEvent("subghz_provisioning") {
    override fun getParamsMap(): Map<String, Any> {
        val paramsMap = mutableMapOf<String, Any>(
            "region_source" to regionSource,
            "is_roaming" to isRoaming
        )
        if (regionNetwork != null) {
            paramsMap["region_network"] = regionNetwork
        }
        if (regionSimOne != null) {
            paramsMap["region_sim_1"] = regionSimOne
        }
        if (regionIp != null) {
            paramsMap["region_ip"] = regionIp
        }
        if (regionSystem != null) {
            paramsMap["region_system"] = regionSystem
        }
        if (regionProvided != null) {
            paramsMap["region_provided"] = regionProvided
        }
        return paramsMap
    }
}

enum class RegionSource(val id: Int) {
    SIM_NETWORK(id = 0),
    SIM_COUNTRY(id = 1),
    GEO_IP(id = 2),
    SYSTEM(id = 3),
    DEFAULT(id = 4)
}
