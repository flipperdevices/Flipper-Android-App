package com.flipperdevices.newfilemanager.impl.composable.dialog

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
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.connection.feature.storage.api.utils.FlipperSymbolFilter
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import flipperapp.components.newfilemanager.impl.generated.resources.Res
import flipperapp.components.newfilemanager.impl.generated.resources.add_dialog_error_empty
import flipperapp.components.newfilemanager.impl.generated.resources.add_dialog_error_too_long
import flipperapp.components.newfilemanager.impl.generated.resources.add_dialog_error_wrong_character
import flipperapp.components.newfilemanager.impl.generated.resources.share_dialog_btn_close
import flipperapp.components.newfilemanager.impl.generated.resources.share_dialog_btn_ok
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private const val FILE_MAX_LENGTH = 128

@Composable
@Suppress("LongMethod")
fun ComposableInputDialog(
    title: StringResource,
    onFinishEdit: (String?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<StringResource?>(null) }

    AlertDialog(
        title = {
            Text(
                text = stringResource(title),
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
                        text = FlipperSymbolFilter.filterUnacceptableSymbolInFileName(it)
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
                    Text(text = stringResource(Res.string.share_dialog_btn_close))
                }
                Button(
                    modifier = Modifier.padding(all = 16.dp),
                    onClick = {
                        if (text.isBlank()) {
                            errorText = Res.string.add_dialog_error_empty
                        } else if (!FlipperSymbolFilter.isAcceptableString(text.replace(".", ""))) {
                            errorText = Res.string.add_dialog_error_wrong_character
                        } else if (text.length > FILE_MAX_LENGTH) {
                            errorText = Res.string.add_dialog_error_too_long
                        } else {
                            onFinishEdit(text)
                        }
                    }
                ) {
                    Text(text = stringResource(Res.string.share_dialog_btn_ok))
                }
            }
        }
    )
}
