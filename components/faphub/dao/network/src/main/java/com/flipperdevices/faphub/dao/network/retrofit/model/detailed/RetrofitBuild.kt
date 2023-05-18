package com.flipperdevices.faphub.dao.network.retrofit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitBuild(
    @SerialName("_id") val id: String,
    @SerialName("build_gfs_id") val gfsId: String,
    @SerialName("sdk") val sdk: RetrofitSdk
)
