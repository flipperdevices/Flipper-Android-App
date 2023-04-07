package com.flipperdevices.selfupdater.api

data class SelfUpdate(
    val version: String,
    val downloadUrl: String,
    val name: String,
)
