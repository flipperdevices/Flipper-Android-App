package com.flipperdevices.faphub.installation.manifest.model

import com.flipperdevices.core.data.SemVer

data class FapManifestVersion(
    val versionUid: String,
    val semVer: SemVer
)
