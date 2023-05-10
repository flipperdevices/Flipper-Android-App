package com.flipperdevices.faphub.dao.network.model

import com.flipperdevices.faphub.dao.api.model.FapDeveloperInformation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationCurrentVersionLinks(
    @SerialName("bundle_urn") val bundleUrl: String,
    @SerialName("manifest_uri") val manifestUrl: String,
    @SerialName("source_code_uri") val sourceCodeUrl: String
) {
    companion object {
        fun ApplicationCurrentVersionLinks.toFapDeveloperInformation(): FapDeveloperInformation {
            return FapDeveloperInformation(
                githubRepositoryLink = sourceCodeUrl,
                manifestRepositoryLink = manifestUrl
            )
        }
    }
}
