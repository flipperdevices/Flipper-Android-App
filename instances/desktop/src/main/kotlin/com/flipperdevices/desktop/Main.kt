package com.flipperdevices.desktop

import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Flipper Desktop") {
        Text("Hello from desktop")
    }
}