package com.flipperdevices.toolstab.impl.composable.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.toolstab.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun RemoteControlsComposable(
    onOpenRemoteControls: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(14.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            RemoteControlsTitle()
            RemoteControls(
                modifier = Modifier.clickableRipple(onClick = onOpenRemoteControls),
            )
        }
    }
}

@Composable
private fun RemoteControlsTitle() = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp)
            .size(24.dp),
        painter = painterResource(DesignSystem.drawable.ic_fileformat_ir),
        contentDescription = stringResource(R.string.remotes_library_infrared_title),
        tint = LocalPalletV2.current.icon.blackAndWhite.default
    )
    Text(
        text = stringResource(R.string.remotes_library_infrared_title),
        style = LocalTypography.current.buttonB16,
        color = LocalPallet.current.text100
    )
}

@Composable
private fun RemoteControls(
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    Image(
        modifier = Modifier
            .padding(start = 12.dp, bottom = 6.dp, top = 6.dp)
            .size(64.dp),
        painter = painterResource(
            if (MaterialTheme.colors.isLight) {
                R.drawable.pic_remotes_library_light
            } else {
                R.drawable.pic_remotes_library_dark
            }
        ),
        contentDescription = stringResource(R.string.remotes_library_title)
    )
    Column(
        Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.remotes_library_title),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text100
        )
        Text(
            text = stringResource(R.string.remotes_library_desc),
            style = LocalTypography.current.subtitleR12,
            color = LocalPallet.current.text30
        )
    }

    Text(
        text = stringResource(R.string.card_beta),
        style = LocalTypography.current.subtitleR12,
        color = LocalPallet.current.text30,
        modifier = Modifier
            .border(
                1.dp,
                LocalPalletV2.current.action.neutral.border.secondary.default,
                RoundedCornerShape(30.dp)
            )
            .clip(RoundedCornerShape(30.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp)
    )

    Icon(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        painter = painterResource(id = DesignSystem.drawable.ic_navigate),
        contentDescription = stringResource(R.string.remotes_library_title),
        tint = LocalPallet.current.iconTint30
    )
}

@Preview
@Composable
private fun RemoteControlsComposablePreview() {
    FlipperThemeInternal {
        RemoteControlsComposable(onOpenRemoteControls = {})
    }
}
