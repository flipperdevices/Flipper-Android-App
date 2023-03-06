package com.flipperdevices.impl.api

import androidx.compose.runtime.Composable

interface SelfUpdaterApi {
    @Composable
    fun CheckAndShowUpdateDialog()
}
