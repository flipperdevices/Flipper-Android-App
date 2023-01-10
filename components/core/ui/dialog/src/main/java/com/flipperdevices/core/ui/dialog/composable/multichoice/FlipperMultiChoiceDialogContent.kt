package com.flipperdevices.core.ui.dialog.composable.multichoice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
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
internal fun FlipperMultiChoiceDialogContent(
    model: FlipperMultiChoiceDialogModel
) = Column(Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    if (model.onDismissRequest != null) {
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp, start = 12.dp)
                    .size(size = 24.dp)
                    .clickableRipple(
                        bounded = false,
                        onClick = model.onDismissRequest
                    ),
                painter = painterResource(DesignSystem.drawable.ic_close),
                tint = LocalPallet.current.iconTint100,
                contentDescription = stringResource(R.string.core_ui_dialog_close)
            )
        }
    }
    if (model.imageComposable != null) {
        Box(modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)) {
            model.imageComposable.invoke()
        }
    }
    if (model.titleComposable != null) {
        Box(modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)) {
            model.titleComposable.invoke()
        }
    }
    if (model.textComposable != null) {
        Box(modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp)) {
            model.textComposable.invoke()
        }
    }

    if (model.buttonComposables.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(
                top = 24.dp
            )
        ) {
            model.buttonComposables.forEach { buttonComposable ->
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = LocalPallet.current.divider12
                )
                buttonComposable()
            }
        }
    }
}
