package com.flipperdevices.core.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetUpStatusBarColor(color: Color) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, color) {
        systemUiController.setStatusBarColor(
            color = color,
            transformColorForLightContent = { color }
        )

        onDispose {}
    }
}

@Composable
fun SetUpNavigationBarColor(color: Color) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, color) {
        systemUiController.setNavigationBarColor(
            color = color,
            transformColorForLightContent = { color }
        )

        onDispose {}
    }
}