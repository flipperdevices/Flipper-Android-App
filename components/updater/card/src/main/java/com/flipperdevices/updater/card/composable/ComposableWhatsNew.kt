package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.UpdateRequest

@Composable
fun ComposableWhatsNew(
    update: UpdateRequest,
    modifier: Modifier = Modifier
) {
    if (update.changelog == null) {
        return
    }
    val rootNavigation = LocalRootNavigation.current
    ComposableWhatsNewButton(
        modifier = modifier.padding(top = 12.dp, end = 8.dp),
        onClick = {
            rootNavigation.push(RootScreenConfig.Changelog(update))
        }
    )
}

@Composable
private fun ComposableWhatsNewButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .border(
                width = 1.dp,
                color = LocalPalletV2.current.action.neutral.border.tertiary.default,
                shape = RoundedCornerShape(30.dp)
            )
            .clickableRipple(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(top = 6.dp, bottom = 6.dp, start = 8.dp, end = 2.dp)
                .size(11.dp),
            painter = painterResource(R.drawable.ic_whatsnew_attention),
            contentDescription = stringResource(R.string.updater_card_updater_title_whatsnew),
            tint = LocalPalletV2.current.action.neutral.icon.secondary.default
        )
        Text(
            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp, end = 8.dp),
            text = stringResource(R.string.updater_card_updater_title_whatsnew),
            color = LocalPalletV2.current.action.neutral.icon.secondary.default,
            style = LocalTypography.current.subtitleM12
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableWhatsNewButtonPreview() {
    FlipperThemeInternal {
        ComposableWhatsNewButton(Modifier, onClick = {})
    }
}
