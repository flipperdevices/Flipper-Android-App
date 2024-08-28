@file:Suppress("CompositionLocalAllowlist")

package com.flipperdevices.remotecontrols.impl.grid.local.composable.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.remotecontrols.grid.saved.impl.R
import com.flipperdevices.core.ui.res.R as SharedRes

@Composable
@Suppress("LongMethod")
internal fun ComposableInfraredDropDown(
    onRemoteInfo: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    isEmulating: Boolean,
    isConnected: Boolean,
    modifier: Modifier = Modifier,
) {
    var isShowMoreOptions by remember { mutableStateOf(false) }
    val onChangeState = { isShowMoreOptions = !isShowMoreOptions }
    val isDropDownEnabled = isConnected && !isEmulating
    val moreIconTint by animateColorAsState(
        if (isDropDownEnabled) {
            LocalPalletV2.current.icon.blackAndWhite.blackOnColor
        } else {
            LocalPalletV2.current.action.neutral.icon.primary.disabled
        }
    )
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            modifier = Modifier
                .clickableRipple(
                    bounded = false,
                    onClick = onChangeState,
                    enabled = isDropDownEnabled
                )
                .size(24.dp),
            tint = moreIconTint,
            painter = painterResource(SharedRes.drawable.ic_more_points),
            contentDescription = null
        )
        DropdownMenu(
            expanded = isShowMoreOptions,
            onDismissRequest = { isShowMoreOptions = false }
        ) {
            ComposableInfraredDropDownItem(
                text = stringResource(R.string.option_remote_info),
                painter = painterResource(R.drawable.ic_how_to_use),
                onClick = {
                    onRemoteInfo.invoke()
                    onChangeState.invoke()
                }
            )
            Divider(modifier = Modifier.padding(horizontal = 8.dp))
            ComposableInfraredDropDownItem(
                text = stringResource(R.string.option_rename),
                painter = painterResource(R.drawable.ic_edit),
                onClick = {
                    onChangeState.invoke()
                    onRename.invoke()
                },
                isActive = !isEmulating
            )
            Divider(modifier = Modifier.padding(horizontal = 8.dp))
            ComposableInfraredDropDownItem(
                text = stringResource(R.string.option_share),
                painter = painterResource(SharedRes.drawable.ic_upload),
                onClick = {
                    onChangeState.invoke()
                    onShare.invoke()
                }
            )
            Divider(modifier = Modifier.padding(horizontal = 8.dp))
            ComposableInfraredDropDownItem(
                text = stringResource(R.string.option_delete),
                painter = painterResource(SharedRes.drawable.ic_trash_icon),
                colorText = LocalPallet.current.keyDelete,
                colorIcon = LocalPallet.current.keyDelete,
                onClick = {
                    onChangeState.invoke()
                    onDelete.invoke()
                }
            )
        }
    }
}

@Composable
private fun ComposableInfraredDropDownItem(
    text: String,
    painter: Painter,
    isActive: Boolean = true,
    colorText: Color = LocalPallet.current.text100,
    colorIcon: Color = LocalPallet.current.text100,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        onClick = {
            if (isActive) {
                onClick()
            }
        },
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painter,
                    tint = if (isActive) {
                        colorIcon
                    } else {
                        LocalPallet.current.keyScreenDisabled
                    },
                    contentDescription = null
                )
                Text(
                    text = text,
                    style = LocalTypography.current.bodyM14,
                    color = if (isActive) {
                        colorText
                    } else {
                        LocalPallet.current.keyScreenDisabled
                    }
                )
            }
        }
    }
}
