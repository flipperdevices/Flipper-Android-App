package com.flipperdevices.core.ui.dialog.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.ktx.letCompose
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun FlipperDialog(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
    closeOnClickOutside: Boolean = true
) {
    // Disable selection on dialog, because on SelectionContainer crash
    DisableSelection {
        Dialog(onDismissRequest = {
            if (closeOnClickOutside) {
                onDismissRequest?.invoke()
            }
        }) {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(LocalPallet.current.backgroundDialog)
            ) {
                FlipperDialogContent(
                    buttons,
                    image = image,
                    title = title,
                    text = text,
                    onDismissRequest = onDismissRequest
                )
            }
        }
    }
}

@Composable
fun FlipperDialog(
    buttonText: String,
    onClickButton: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    title: String? = null,
    imageComposable: (@Composable () -> Unit)? = painter?.letCompose { painterNotNullable ->
        Image(
            painter = painterNotNullable,
            contentDescription = title
        )
    },
    titleComposable: (@Composable () -> Unit)? = title?.letCompose { titleNotNullable ->
        Text(
            text = titleNotNullable,
            style = LocalTypography.current.bodyM14,
            textAlign = TextAlign.Center,
            color = LocalPallet.current.text100
        )
    },
    text: String? = null,
    textComposable: (@Composable () -> Unit)? = text?.letCompose {
        Text(
            text = text,
            color = LocalPallet.current.text40,
            style = LocalTypography.current.bodyR14,
            textAlign = TextAlign.Center
        )
    },
    buttonComposable: @Composable () -> Unit = {
        ComposableFlipperButton(
            modifier = Modifier.fillMaxWidth(),
            text = buttonText,
            onClick = onClickButton,
            textPadding = PaddingValues(12.dp)
        )
    },
    onDismissRequest: (() -> Unit)? = null,
    closeOnClickOutside: Boolean = true
) {
    FlipperDialog(
        buttonComposable,
        modifier,
        image = imageComposable,
        title = titleComposable,
        text = textComposable,
        onDismissRequest,
        closeOnClickOutside
    )
}
