package com.flipperdevices.keyemulate.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.image.Picture
import com.flipperdevices.keyemulate.composable.common.InternalComposableEmulateButtonWithText
import com.flipperdevices.keyemulate.composable.common.button.ComposableEmulateButton
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, KeyEmulateUiApi::class)
class KeyEmulateUiApiImpl @Inject constructor() : KeyEmulateUiApi {
    @Composable
    override fun ComposableEmulateButtonRaw(
        modifier: Modifier,
        buttonContentModifier: Modifier,
        emulateProgress: EmulateProgress?,
        textId: Int,
        picture: Picture?,
        color: Color,
        progressColor: Color
    ) {
        ComposableEmulateButton(
            textId,
            picture,
            color,
            modifier,
            buttonContentModifier,
            emulateProgress,
            progressColor
        )
    }

    @Composable
    override fun ComposableEmulateButtonWithText(
        modifier: Modifier,
        buttonModifier: Modifier,
        progress: EmulateProgress?,
        buttonTextId: Int,
        textId: Int?,
        iconId: Int?,
        picture: Picture?,
        color: Color,
        progressColor: Color
    ) {
        InternalComposableEmulateButtonWithText(
            modifier = modifier,
            buttonModifier = buttonModifier,
            progress = progress,
            buttonTextId = buttonTextId,
            textId = textId,
            iconId = iconId,
            picture = picture,
            color = color,
            progressColor = progressColor
        )
    }
}
