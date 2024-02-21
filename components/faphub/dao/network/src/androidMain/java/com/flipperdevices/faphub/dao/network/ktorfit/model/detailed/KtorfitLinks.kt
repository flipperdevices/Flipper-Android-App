package com.flipperdevices.faphub.dao.network.ktorfit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitLinks(
    @SerialName("bundle_uri") val bundleUrl: String,
    @SerialName("manifest_uri") val manifestUrl: String,
    @SerialName("source_code") val sourceCode: KtorfitSourceCodeLinks,

)
