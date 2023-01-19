package com.flipperdevices.core.ui.dialog.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.R
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun FlipperDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null
) = Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    if (onDismissRequest != null) {
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp, start = 12.dp)
                    .size(size = 24.dp)
                    .clickableRipple(bounded = false, onClick = onDismissRequest),
                painter = painterResource(DesignSystem.drawable.ic_close),
                tint = LocalPallet.current.iconTint100,
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
