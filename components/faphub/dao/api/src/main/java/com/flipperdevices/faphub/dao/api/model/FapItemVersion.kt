package com.flipperdevices.faphub.dao.api.model

import androidx.compose.runtime.Stable
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.target.model.FlipperTarget

@Stable
data class FapItemVersion(
    val id: String,
    val version: SemVer,
    val target: FlipperTarget,
    val buildState: FapBuildState,
    val sdkApi: SemVer?
)
