package com.flipperdevices.keyscreen.api

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

interface KeyEmulateUiApi {
    @Composable
    fun ComposableEmulateButtonRaw(
        modifier: Modifier,
        buttonContentModifier: Modifier,
        emulateProgress: EmulateProgress?,
        @StringRes textId: Int,
        picture: Picture?,
        color: Color,
        progressColor: Color
    )

    @Composable
    fun ComposableEmulateButtonWithText(
        modifier: Modifier,
        buttonModifier: Modifier,
        progress: EmulateProgress?,
        @StringRes buttonTextId: Int,
        @StringRes textId: Int?,
        @DrawableRes iconId: Int?,
        picture: Picture?,
        color: Color,
        progressColor: Color
    )
}
