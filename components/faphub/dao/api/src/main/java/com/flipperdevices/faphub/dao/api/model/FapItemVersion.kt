package com.flipperdevices.faphub.dao.api.model

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.target.model.FlipperTarget

data class FapItemVersion(
    val id: String,
    val version: SemVer,
    val target: FlipperTarget,
    val buildState: FapBuildState,
    val sdkApi: SemVer?
)
