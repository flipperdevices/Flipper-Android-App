package com.flipperdevices.selfupdater.source.github.model

data class GithubUpdate(
    val version: String,
    val downloadUrl: String,
    val name: String,
)
