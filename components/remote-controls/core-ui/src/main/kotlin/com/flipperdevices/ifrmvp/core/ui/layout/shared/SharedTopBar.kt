package com.flipperdevices.ifrmvp.core.ui.layout.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun SharedTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "",
    subtitle: String = "",
    actions: @Composable BoxScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(LocalPalletV2.current.surface.navBar.body.accentBrand)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(weight = 1f),
            contentAlignment = Alignment.CenterStart,
            content = {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickableRipple(bounded = false, onClick = onBackClick),
                    painter = painterResource(DesignSystem.drawable.ic_back),
                    contentDescription = null,
                    tint = LocalPalletV2.current.icon.blackAndWhite.blackOnColor
                )
            }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(weight = 2f, fill = false)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = title,
                color = LocalPalletV2.current.text.title.blackOnColor,
                style = LocalTypography.current.titleEB18,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                color = LocalPalletV2.current.text.title.blackOnColor,
                style = LocalTypography.current.subtitleM12
            )
        }
        Box(
            modifier = Modifier.weight(weight = 1f),
            contentAlignment = Alignment.CenterEnd,
            content = {
                actions.invoke(this)
            }
        )
    }
}

@Preview
@Composable
private fun SharedTopBarPreview() {
    FlipperThemeInternal {
        Column {
            SharedTopBar(
                title = "Title Title Title Title Title Title Title",
                subtitle = "Subtitle",
                onBackClick = {},
            )
            SharedTopBar(
                title = "Title Title Title Title Title Title Title",
                subtitle = "Subtitle",
                onBackClick = {},
                actions = {
                    Row {
                        repeat(2) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickableRipple(bounded = false, onClick = {}),
                                painter = painterResource(DesignSystem.drawable.ic_back),
                                contentDescription = null,
                                tint = LocalPalletV2.current.icon.blackAndWhite.blackOnColor
                            )
                        }
                    }
                }
            )
            SharedTopBar(
                title = "Title Title Title Title Title Title Title",
                subtitle = "Subtitle",
                onBackClick = {},
                actions = {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Action",
                            color = LocalPalletV2.current.text.title.blackOnColor,
                            style = LocalTypography.current.titleEB18,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .clickableRipple(bounded = false, onClick = {}),
                            painter = painterResource(DesignSystem.drawable.ic_back),
                            contentDescription = null,
                            tint = LocalPalletV2.current.icon.blackAndWhite.blackOnColor
                        )
                    }
                }
            )
        }
    }
}
