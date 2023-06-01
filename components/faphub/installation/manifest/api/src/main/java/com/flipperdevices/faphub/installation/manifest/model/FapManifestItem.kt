package com.flipperdevices.faphub.installation.manifest.model

data class FapManifestItem(
    val applicationAlias: String,
    val uid: String,
    val version: FapManifestVersion,
    val path: String
)
