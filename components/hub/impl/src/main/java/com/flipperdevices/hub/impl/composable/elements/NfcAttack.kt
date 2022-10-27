package com.flipperdevices.hub.impl.composable.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.hub.impl.R
import com.flipperdevices.hub.impl.viewmodel.NfcAttackViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun NfcAttack(onOpenAttack: () -> Unit) {
    Card(
        modifier = Modifier.padding(14.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(),
                    onClick = onOpenAttack
                )
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 2.dp)
                    .size(30.dp),
                painter = painterResource(DesignSystem.drawable.ic_fileformat_nfc),
                contentDescription = stringResource(R.string.hub_hfc_title),
                tint = LocalPallet.current.text100
            )
            NfcAttackDescription()
        }
    }
}

@Composable
private fun NfcAttackDescription() = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    Column(
        Modifier.weight(1f)
    ) {
        Text(
            modifier = Modifier.padding(
                top = 12.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 4.dp
            ),
            text = stringResource(R.string.hub_hfc_title),
            style = LocalTypography.current.buttonM16,
            color = LocalPallet.current.text100
        )

        Text(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            text = stringResource(R.string.hub_hfc_desc),
            style = LocalTypography.current.subtitleR12,
            color = LocalPallet.current.text30
        )
    }

    val nfcAttackViewModel: NfcAttackViewModel = tangleViewModel()
    val notificationCount by nfcAttackViewModel.getNfcAttackNotificationCountState()
        .collectAsState()
    if (notificationCount > 0) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(LocalPallet.current.accent),
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
        painter = painterResource(id = DesignSystem.drawable.ic_navigate),
        contentDescription = stringResource(R.string.hub_hfc_title),
        tint = LocalPallet.current.iconTint30
    )
}
