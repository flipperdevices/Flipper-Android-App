package com.flipperdevices.faphub.dao.network.retrofit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitLinks(
    @SerialName("bundle_uri") val bundleUrl: String,
    @SerialName("manifest_uri") val manifestUrl: String,
    @SerialName("source_code") val sourceCode: RetrofitSourceCodeLinks,

)
