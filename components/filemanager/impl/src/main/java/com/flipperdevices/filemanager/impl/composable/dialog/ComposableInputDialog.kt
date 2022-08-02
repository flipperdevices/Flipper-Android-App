package com.flipperdevices.filemanager.impl.composable.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.api.utils.FlipperSymbolFilter
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.impl.R

private const val FILE_MAX_LENGTH = 128

@Composable
fun ComposableInputDialog(
    titleId: Int,
    onFinishEdit: (String?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        title = {
            Text(
                text = stringResource(titleId),
                style = LocalTypography.current.titleM18
            )
        },
        text = {
            Column {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = {
                        errorText = null
                        text = FlipperSymbolFilter.filterUnacceptableSymbol(it)
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = LocalPallet.current.text100
                    )
                )
                errorText?.let {
                    Text(
                        text = stringResource(it),
                        color = LocalPallet.current.warningColor
                    )
                }
            }
        },
        onDismissRequest = { onFinishEdit(null) },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    modifier = Modifier.padding(all = 16.dp),
                    onClick = { onFinishEdit(null) }
                ) {
                    Text(text = stringResource(R.string.share_dialog_btn_close))
                }
                Button(
                    modifier = Modifier.padding(all = 16.dp),
                    onClick = {
                        if (text.isBlank()) {
                            errorText = R.string.add_dialog_error_empty
                        } else if (!FlipperSymbolFilter.isAcceptableString(text)) {
                            errorText = R.string.add_dialog_error_wrong_character
                        } else if (text.length > FILE_MAX_LENGTH) {
                            errorText = R.string.add_dialog_error_too_long
                        } else onFinishEdit(text)
                    }
                ) {
                    Text(text = stringResource(R.string.share_dialog_btn_ok))
                }
            }
        }
    )
}
