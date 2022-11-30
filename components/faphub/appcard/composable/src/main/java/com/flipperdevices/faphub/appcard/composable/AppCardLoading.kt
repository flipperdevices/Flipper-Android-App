package com.flipperdevices.faphub.appcard.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppCardLoading(modifier: Modifier) {
    Box(modifier.fillMaxWidth()) {
        CircularProgressIndicator()
    }
}