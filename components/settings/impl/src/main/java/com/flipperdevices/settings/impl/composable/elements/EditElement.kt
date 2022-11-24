package com.flipperdevices.settings.impl.composable.elements

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.ktx.letCompose
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.settings.impl.R

@Composable
fun EditElement(
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    @StringRes dialogTitle: Int? = null,
    default: String?,
    onEdit: (String) -> Unit
) {
    var isOpenEditDialog by remember { mutableStateOf(false) }

    EditElementRow(titleId, descriptionId, onClick = {
        isOpenEditDialog = true
    })

    if (isOpenEditDialog) {
        EditElementDialog(
            titleId = dialogTitle,
            default = default,
            onClose = {
                isOpenEditDialog = false
                if (!it.isNullOrBlank()) {
                    onEdit(it)
                }
            }
        )
    }
}

@Composable
private fun EditElementRow(
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = onClick
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            Modifier.weight(weight = 1f),
            titleId = titleId,
            descriptionId = descriptionId
        )
        Icon(
            modifier = Modifier
                .size(size = 42.dp)
                .padding(16.dp),
            painter = painterResource(DesignSystem.drawable.ic_navigate),
            tint = LocalPallet.current.iconTint30,
            contentDescription = null
        )
    }
}

@Composable
private fun EditElementDialog(
    @StringRes titleId: Int? = null,
    default: String?,
    onClose: (String?) -> Unit
) {
    var text by remember { mutableStateOf(default ?: "") }

    FlipperDialog(
        buttonTextId = R.string.element_edit_btn,
        onClickButton = { onClose(text) },
        onDismissRequest = { onClose(null) },
        titleComposable = titleId?.letCompose {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(titleId),
                style = LocalTypography.current.titleR18,
                color = LocalPallet.current.text100
            )
        },
        textComposable = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                value = text,
                onValueChange = { text = it },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = LocalPallet.current.text100
                ),
                singleLine = true
            )
        },
        cancelButtonActive = false
    )
}
