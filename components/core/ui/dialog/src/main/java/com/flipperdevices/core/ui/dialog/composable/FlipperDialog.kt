package com.flipperdevices.core.ui.dialog.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    @StringRes buttonTextId: Int,
    onClickButton: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes imageId: Int? = null,
    @StringRes titleId: Int? = null,
    imageComposable: (@Composable () -> Unit)? = imageId?.letCompose { imageIdNotNullable ->
        Image(
            painter = painterResource(imageIdNotNullable),
            contentDescription = titleId?.let { stringResource(it) }
        )
    },
    titleComposable: (@Composable () -> Unit)? = titleId?.letCompose { titleIdNotNullable ->
        Text(
            text = stringResource(titleIdNotNullable),
            style = LocalTypography.current.bodyM14,
            textAlign = TextAlign.Center,
            color = LocalPallet.current.text100
        )
    },
    @StringRes textId: Int? = null,
    textComposable: (@Composable () -> Unit)? = textId?.letCompose {
        Text(
            text = stringResource(textId),
            color = LocalPallet.current.text40,
            style = LocalTypography.current.bodyR14,
            textAlign = TextAlign.Center
        )
    },
    buttonComposable: @Composable () -> Unit = {
        ComposableFlipperButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(buttonTextId),
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
