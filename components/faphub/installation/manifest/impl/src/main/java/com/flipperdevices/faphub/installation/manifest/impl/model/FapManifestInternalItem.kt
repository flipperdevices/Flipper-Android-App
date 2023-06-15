package com.flipperdevices.faphub.installation.manifest.impl.model

import com.flipperdevices.core.data.SemVer

data class FapManifestInternalItem(
    val applicationAlias: String,
    val uid: String,
    val versionUid: String,
    val path: String,
    val fullName: String,
    val iconBase64: String?,
    val sdkApi: SemVer?
)
