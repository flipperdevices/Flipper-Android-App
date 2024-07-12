package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("DeviceCategory")
class DeviceCategory(
    @SerialName("id")
    val id: Long,
    @SerialName("meta")
    val meta: CategoryMeta
)
