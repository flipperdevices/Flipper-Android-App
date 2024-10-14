package com.flipperdevices.core.ui.dialog.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun FlipperDialogAndroid(
    @StringRes buttonTextId: Int,
    onClickButton: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes imageId: Int? = null,
    @StringRes titleId: Int? = null,
    @StringRes textId: Int? = null,
    onDismissRequest: (() -> Unit)? = null,
    closeOnClickOutside: Boolean = true
) {
    FlipperDialog(
        buttonText = stringResource(buttonTextId),
        onClickButton = onClickButton,
        onDismissRequest = onDismissRequest,
        closeOnClickOutside = closeOnClickOutside,
        text = textId?.let { stringResource(it) },
        painter = imageId?.let { painterResource(it) },
        modifier = modifier,
        title = titleId?.let { stringResource(it) }
    )
}
