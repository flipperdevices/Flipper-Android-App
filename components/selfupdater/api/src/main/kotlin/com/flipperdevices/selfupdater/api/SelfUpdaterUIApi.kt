package com.flipperdevices.selfupdater.api

import androidx.compose.runtime.Composable

interface SelfUpdaterUIApi {
    @Composable
    fun CheckAndShowUpdateDialog()
}
