package com.flipperdevices.core.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetUpStatusBarColor(color: Color, darkIcon: Boolean? = null) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, color) {
        if (darkIcon == null) {
            systemUiController.setStatusBarColor(
                color = color,
                transformColorForLightContent = { color },
            )
        } else {
            systemUiController.setStatusBarColor(
                color = color,
                transformColorForLightContent = { color },
                darkIcons = darkIcon
            )
        }

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
