package com.flipperdevices.keyemulate.composable.common.button

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.Picture
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyemulate.composable.common.button.sweep.getEmulateProgressBrush
import com.flipperdevices.keyemulate.model.EmulateProgress

private const val BUTTON_HEIGHT_DP = 56

@Composable
@Suppress("LongParameterList")
fun ComposableEmulateButton(
    @StringRes textId: Int,
    picture: Picture?,
    color: Color,
    modifier: Modifier = Modifier,
    buttonContentModifier: Modifier = Modifier,
    emulateProgress: EmulateProgress? = null,
    progressColor: Color = Color.Transparent
) {
    ComposableEmulateProgress(
        modifier = modifier,
        emulateProgress = emulateProgress,
        progressColor = progressColor
    ) { contentModifier ->
        ComposableEmulateContent(
            modifier = contentModifier.then(buttonContentModifier),
            text = stringResource(textId),
            color = color,
            picture = picture
        )
    }
}

@Composable
private fun ComposableEmulateProgress(
    emulateProgress: EmulateProgress?,
    progressColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Box(
        modifier = modifier
            .height(BUTTON_HEIGHT_DP.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                getEmulateProgressBrush(
                    emulateProgress,
                    backgroundColor = progressColor.copy(alpha = 0.4f),
                    cursorColor = progressColor
                )
            )
    ) {
        content(Modifier.padding(all = 4.dp))
    }
}

@Composable
private fun ComposableEmulateContent(
    text: String,
    picture: Picture?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(color),
        contentAlignment = Alignment.CenterStart
    ) {
        picture?.Draw(Modifier.padding(vertical = 8.dp, horizontal = 12.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            style = LocalTypography.current.flipperAction,
            color = LocalPallet.current.onFlipperButton,
            textAlign = TextAlign.Center
        )
    }
}
