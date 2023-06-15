package com.flipperdevices.faphub.installation.manifest.impl.model

data class FapManifestInternalItem(
    val applicationAlias: String,
    val uid: String,
    val versionUid: String,
    val path: String,
    val fullName: String,
    val iconBase64: String?
)
