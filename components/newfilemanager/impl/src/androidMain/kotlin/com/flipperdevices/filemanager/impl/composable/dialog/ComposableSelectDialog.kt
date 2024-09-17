package com.flipperdevices.filemanager.impl.composable.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableSelectDialog(
    options: IntArray,
    onSelect: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { onSelect(null) }) {
        Column(modifier = modifier.background(LocalPallet.current.backgroundDialog)) {
            options.forEachIndexed { index, elementTextId ->
                ComposableDialogOption(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textId = elementTextId,
                    onClick = {
                        onSelect(elementTextId)
                    }
                )
                if (index != options.lastIndex) {
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = LocalPallet.current.divider12
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposableDialogOption(
    @StringRes textId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .clickableRipple(onClick = onClick)
            .then(modifier),
        text = stringResource(textId),
        style = LocalTypography.current.titleM18
    )
}
