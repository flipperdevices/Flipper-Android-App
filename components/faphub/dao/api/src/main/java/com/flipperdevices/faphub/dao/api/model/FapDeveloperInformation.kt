package com.flipperdevices.faphub.dao.api.model

import androidx.compose.runtime.Stable

@Stable
data class FapDeveloperInformation(
    val githubRepositoryLink: String,
    val manifestRepositoryLink: String
)
