package com.flipperdevices.hub.impl.composable.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableHubElement(
    @DrawableRes iconId: Int,
    @StringRes titleId: Int,
    @StringRes descriptionId: Int,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    notificationCount: Int = 0
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .clickableRipple(onClick = onOpen)
        ) {
            ComposableHubElementHead(
                iconId = iconId,
                titleId = titleId,
                notificationCount = notificationCount
            )
            ComposableHubElementDescription(
                titleId = titleId,
                descriptionId = descriptionId
            )
        }
    }
}

@Composable
private fun ComposableHubElementHead(
    @DrawableRes iconId: Int,
    @StringRes titleId: Int,
    notificationCount: Int = 0
) = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    Box(Modifier.weight(1f)) {
        Icon(
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 2.dp)
                .size(30.dp),
            painter = painterResource(iconId),
            contentDescription = stringResource(titleId),
            tint = LocalPallet.current.text100
        )
    }
    if (notificationCount > 0) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(LocalPallet.current.updateProgressGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = notificationCount.toString(),
                style = LocalTypography.current.monoSpaceM10,
                color = LocalPallet.current.onFlipperButton
            )
        }
    }
    Icon(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        painter = painterResource(id = R.drawable.ic_navigate),
        contentDescription = stringResource(titleId),
        tint = LocalPallet.current.iconTint30
    )
}

@Composable
private fun ColumnScope.ComposableHubElementDescription(
    @StringRes titleId: Int,
    @StringRes descriptionId: Int
) {
    Text(
        modifier = Modifier.padding(
            top = 12.dp,
            start = 12.dp,
            end = 12.dp,
            bottom = 4.dp
        ),
        text = stringResource(titleId),
        style = LocalTypography.current.buttonM16,
        color = LocalPallet.current.text100
    )

    Text(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
        text = stringResource(descriptionId),
        style = LocalTypography.current.subtitleR12,
        color = LocalPallet.current.text30
    )
}
