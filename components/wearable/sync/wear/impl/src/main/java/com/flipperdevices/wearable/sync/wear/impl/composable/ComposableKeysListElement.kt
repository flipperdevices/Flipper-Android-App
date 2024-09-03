package com.flipperdevices.wearable.sync.wear.impl.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.icon
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.wearable.sync.wear.impl.model.FlipperWearKey
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableKeysListCategory(
    @StringRes textId: Int,
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconModifier = Modifier
            .padding(14.dp)
            .size(24.dp)
        if (iconId == null) {
            Box(
                modifier = iconModifier
            )
        } else {
            Image(
                modifier = iconModifier,
                painter = painterResource(iconId),
                contentDescription = null
            )
        }
        Text(
            text = stringResource(textId),
            style = LocalTypography.current.titleB18
        )
    }
}

@Composable
fun ComposableKeysListElement(
    flipperWearKey: FlipperWearKey,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val type = flipperWearKey.path.path.keyType
    val icon = type?.icon ?: DesignSystem.drawable.ic_fileformat_unknown
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(7.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(FlipperKeyType.colorByFlipperKeyType(type))
        ) {
            Icon(
                modifier = Modifier
                    .padding(7.dp)
                    .size(24.dp),
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Black
            )
        }

        Text(
            text = flipperWearKey.path.path.nameWithoutExtension,
            style = LocalTypography.current.bodyM14
        )
    }
}
