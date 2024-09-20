package com.flipperdevices.newfilemanager.impl.composable.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComposableSelectDialog(
    options: Array<StringResource>,
    onSelect: (StringResource?) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { onSelect(null) }) {
        Column(modifier = modifier.background(LocalPallet.current.backgroundDialog)) {
            options.forEachIndexed { index, elementText ->
                ComposableDialogOption(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    text = elementText,
                    onClick = {
                        onSelect(elementText)
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
    text: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .clickableRipple(onClick = onClick)
            .then(modifier),
        text = stringResource(text),
        style = LocalTypography.current.titleM18
    )
}
