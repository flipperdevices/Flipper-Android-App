package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import com.flipperdevices.core.ktx.jre.then
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.R
import com.flipperdevices.faphub.fapscreen.impl.composable.dialogs.ComposableFapHideConfirmDialog

@Composable
fun ComposableFapHide(
    fapItem: FapItem?,
    onClick: () -> Unit,
    isHidden: Boolean,
    modifier: Modifier = Modifier,
) {
    var hideDialogVisible by remember { mutableStateOf(false) }

    if (hideDialogVisible && fapItem != null) {
        ComposableFapHideConfirmDialog(
            fapItem = fapItem,
            onConfirm = {
                hideDialogVisible = false
                onClick()
            },
            onDismiss = {
                hideDialogVisible = false
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableRipple(onClick = {
                if (isHidden) {
                    onClick()
                } else {
                    hideDialogVisible = true
                }
            })
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val text = if (isHidden) {
            stringResource(R.string.fapscreen_developer_unhide)
        } else {
            stringResource(R.string.fapscreen_developer_hide)
        }
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_hide),
            contentDescription = text,
            tint = LocalPallet.current.warningColor
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = text,
            style = LocalTypography.current.bodyR14,
            color = LocalPallet.current.warningColor
        )
    }
}
