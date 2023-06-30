@file:Suppress("CompositionLocalAllowlist")

package com.flipperdevices.infrared.impl.composable.components.bar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.infrared.impl.R
import com.flipperdevices.infrared.impl.composable.components.ComposableInfraredDialogHowToUse
import com.flipperdevices.core.ui.res.R as SharedRes

private val LocalOnChangeState = compositionLocalOf<() -> Unit> { error("Not realization") }

@Composable
@Suppress("LongMethod")
internal fun ComposableInfraredDropDown(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    onFavorite: () -> Unit,
    onShare: () -> Unit,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
) {
    var isShowHowToUseDialog by remember { mutableStateOf(false) }
    ComposableInfraredDialogHowToUse(isShowHowToUseDialog) { isShowHowToUseDialog = false }

    var isShowMoreOptions by remember { mutableStateOf(false) }
    val onChangeState = { isShowMoreOptions = !isShowMoreOptions }

    CompositionLocalProvider(LocalOnChangeState provides onChangeState) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                modifier = Modifier
                    .clickableRipple(bounded = false, onClick = onChangeState)
                    .size(24.dp),
                painter = painterResource(SharedRes.drawable.ic_more_points),
                contentDescription = null
            )
            DropdownMenu(
                expanded = isShowMoreOptions,
                onDismissRequest = { isShowMoreOptions = false }
            ) {
                val isFavoriteIcon = if (isFavorite) {
                    SharedRes.drawable.ic_star_enabled
                } else {
                    SharedRes.drawable.ic_star_disabled
                }
                val isFavoriteText = if (isFavorite) {
                    R.string.infrared_options_favorite_remove
                } else {
                    R.string.infrared_options_favorite_add
                }

                ComposableInfraredDropDownItem(
                    textId = isFavoriteText,
                    iconId = isFavoriteIcon,
                    colorIcon = LocalPallet.current.keyFavorite,
                    onClick = onFavorite
                )
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                ComposableInfraredDropDownItem(
                    textId = R.string.infrared_options_edit,
                    iconId = R.drawable.ic_edit,
                    onClick = onEdit
                )
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                ComposableInfraredDropDownItem(
                    textId = R.string.infrared_options_rename,
                    iconId = SharedRes.drawable.ic_edit_icon,
                    onClick = onRename
                )
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                ComposableInfraredDropDownItem(
                    textId = R.string.infrared_options_share,
                    iconId = SharedRes.drawable.ic_upload,
                    onClick = onShare
                )
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                ComposableInfraredDropDownItem(
                    textId = R.string.infrared_options_how_to_use,
                    iconId = R.drawable.ic_how_to_use,
                    onClick = { isShowHowToUseDialog = true }
                )
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                ComposableInfraredDropDownItem(
                    textId = R.string.infrared_options_delete,
                    iconId = SharedRes.drawable.ic_trash_icon,
                    colorText = LocalPallet.current.keyDelete,
                    colorIcon = LocalPallet.current.keyDelete,
                    onClick = onDelete
                )
            }
        }
    }
}

@Composable
private fun ComposableInfraredDropDownItem(
    @StringRes textId: Int,
    @DrawableRes iconId: Int,
    colorText: Color = LocalPallet.current.text100,
    colorIcon: Color = LocalPallet.current.text100,
    onClick: () -> Unit,
) {
    val onChangeState = LocalOnChangeState.current

    DropdownMenuItem(
        onClick = {
            onChangeState()
            onClick()
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
                    painter = painterResource(iconId),
                    tint = colorIcon,
                    contentDescription = null
                )
                Text(
                    text = stringResource(id = textId),
                    style = LocalTypography.current.bodyM14,
                    color = colorText
                )
            }
        }
    }
}
