package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.core.ui.composable.painterResourceByKey

@Composable
fun RowScope.ComposableActionFlipper(
    @DrawableRes iconId: Int,
    @StringRes descriptionId: Int,
    @ColorRes descriptionColorId: Int = com.flipperdevices.core.ui.R.color.white_100,
    @ColorRes tintId: Int = com.flipperdevices.core.ui.R.color.white_100,
    onClick: () -> Unit,
) {
    val descriptionText = stringResource(descriptionId)

    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(30.dp))
            .weight(1f)
            .background(colorResource(id = DesignSystem.color.accent_secondary))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(size = 36.dp),
                painter = painterResourceByKey(iconId),
                contentDescription = descriptionText,
                tint = colorResource(tintId)
            )
            Text(
                text = descriptionText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorResource(descriptionColorId)
            )
        }
    }
}