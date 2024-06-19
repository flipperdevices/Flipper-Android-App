package com.flipperdevices.toolstab.impl.composable.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.toolstab.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun MifareClassicComposable(
    hasMfKey32Notification: Boolean,
    onOpenMfKey32: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(14.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            MifareClassicTitle()
            MifareClassicMfKey32(
                modifier = Modifier.clickableRipple(onClick = onOpenMfKey32),
                hasNotification = hasMfKey32Notification
            )
        }
    }
}

@Composable
private fun MifareClassicTitle() = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp)
            .size(24.dp),
        painter = painterResource(DesignSystem.drawable.ic_fileformat_nfc),
        contentDescription = stringResource(R.string.nfcattack_mifare_classic_title),
        tint = LocalPallet.current.text100
    )
    Text(
        text = stringResource(R.string.nfcattack_mifare_classic_title),
        style = LocalTypography.current.buttonB16,
        color = LocalPallet.current.text100
    )
}

@Composable
private fun MifareClassicMfKey32(
    hasNotification: Boolean,
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
                R.drawable.pic_detect_reader
            } else {
                R.drawable.pic_detect_reader_black
            }
        ),
        contentDescription = stringResource(R.string.nfcattack_mifare_classic_mfkey32_title)
    )
    Column(
        Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.nfcattack_mifare_classic_mfkey32_title),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text100
        )
        Text(
            text = stringResource(R.string.nfcattack_mifare_classic_mfkey32_desc),
            style = LocalTypography.current.subtitleR12,
            color = LocalPallet.current.text30
        )
    }

    if (hasNotification) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(LocalPalletV2.current.action.blackAndWhite.border.whiteOnColor)
                .padding(1.dp)
                .clip(CircleShape)
                .background(LocalPalletV2.current.action.fwUpdate.background.primary.default),
        )
    }

    Icon(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        painter = painterResource(id = DesignSystem.drawable.ic_navigate),
        contentDescription = stringResource(R.string.nfcattack_mifare_classic_mfkey32_title),
        tint = LocalPallet.current.iconTint30
    )
}
