package com.flipperdevices.faphub.installation.manifest.model

import com.flipperdevices.core.data.SemVer

data class FapManifestEnrichedItem(
    val fapManifestItem: FapManifestItem,
    val numberVersion: SemVer
)
