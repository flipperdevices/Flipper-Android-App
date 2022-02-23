package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.painterResourceByKey

@Composable
fun ComposableActionRow(
    @DrawableRes iconId: Int,
    @ColorRes tintId: Int = DesignSystem.color.black_100,
    @StringRes descriptionId: Int,
    @ColorRes descriptionColorId: Int = DesignSystem.color.black_100,
    onClick: () -> Unit
) {
    ComposableActionRowInternal(
        iconId = iconId,
        tintId = tintId,
        descriptionId = descriptionId,
        descriptionColorId = descriptionColorId,
        onClick = onClick
    )
}

@Composable
fun ComposableActionRowInProgress(
    @StringRes descriptionId: Int,
    @ColorRes descriptionColorId: Int = DesignSystem.color.black_100,
) {
    ComposableActionRowInternal(
        iconId = null,
        descriptionId = descriptionId,
        descriptionColorId = descriptionColorId,
        isProgress = true,
        onClick = null
    )
}

@Composable
@Suppress("LongParameterList")
private fun ComposableActionRowInternal(
    @DrawableRes iconId: Int? = null,
    @ColorRes tintId: Int = DesignSystem.color.black_100,
    @StringRes descriptionId: Int,
    @ColorRes descriptionColorId: Int = DesignSystem.color.black_100,
    isProgress: Boolean = false,
    onClick: (() -> Unit)?
) {
    var modifierForRow = Modifier
        .fillMaxWidth()

    if (!isProgress && onClick != null) {
        modifierForRow = modifierForRow.clickable(
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple()
        )
    }
    modifierForRow = modifierForRow
        .padding(horizontal = 24.dp, vertical = 10.dp)

    Row(
        modifier = modifierForRow,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableActionContent(iconId, tintId, descriptionId, descriptionColorId, isProgress)
    }
}

@Composable
private fun RowScope.ComposableActionContent(
    @DrawableRes iconId: Int? = null,
    @ColorRes tintId: Int = DesignSystem.color.black_100,
    @StringRes descriptionId: Int,
    @ColorRes descriptionColorId: Int = DesignSystem.color.black_100,
    isProgress: Boolean = false
) {
    val descriptionText = stringResource(descriptionId)

    if (isProgress) {
        CircularProgressIndicator(
            modifier = Modifier.size(size = 24.dp)
        )
    } else Icon(
        modifier = Modifier.size(size = 24.dp),
        painter = painterResourceByKey(iconId!!),
        contentDescription = descriptionText,
        tint = colorResource(tintId)
    )

    Text(
        modifier = Modifier.padding(start = 10.dp),
        text = stringResource(descriptionId),
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        color = colorResource(descriptionColorId)
    )
}
