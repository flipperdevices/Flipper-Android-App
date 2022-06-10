package com.flipperdevices.core.ui.dialog.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.R

@Composable
internal fun FlipperDialogContent(
    image: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    buttons: @Composable () -> Unit,
    onDismissRequest: (() -> Unit)? = null
) = Column(Modifier) {
    if (onDismissRequest != null) {
        Box(
            Modifier.fillMaxWidth().clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = onDismissRequest
            ),
            contentAlignment = Alignment.CenterEnd
        ) {
            Image(
                modifier = Modifier
                    .size(size = 24.dp)
                    .padding(top = 12.dp, end = 12.dp, start = 12.dp),
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.core_ui_dialog_close)
            )
        }
    }
    if (image != null) {
        Box(modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)) {
            image()
        }
    }
    if (title != null) {
        Box(modifier = Modifier.padding(top = 24.dp, start = 12.dp, end = 12.dp)) {
            title()
        }
    }
    if (text != null) {
        Box(modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp)) {
            text()
        }
    }

    Box(
        modifier = Modifier.padding(
            top = 24.dp,
            start = 12.dp,
            end = 12.dp,
            bottom = 12.dp
        )
    ) {
        buttons()
    }
}
