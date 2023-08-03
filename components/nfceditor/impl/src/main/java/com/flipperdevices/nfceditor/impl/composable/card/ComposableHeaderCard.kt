package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfceditor.impl.R
import com.flipperdevices.nfceditor.impl.model.NfcEditorCardType
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableHeaderCard(
    cardType: NfcEditorCardType,
    isOpened: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ComposableNameCard(cardType)

        val imageId = if (isOpened) {
            DesignSystem.drawable.ic_more_revert
        } else {
            DesignSystem.drawable.ic_more
        }

        Icon(
            modifier = Modifier.clickable(onClick = onClick),
            painter = painterResource(id = imageId),
            contentDescription = "",
            tint = LocalPallet.current.onNfcCard
        )
    }
}

@Composable
private fun ComposableNameCard(cardType: NfcEditorCardType) {
    val nameCard = when (cardType) {
        NfcEditorCardType.MF_1K -> stringResource(R.string.nfc_card_title_1k)
        NfcEditorCardType.MF_4K -> stringResource(R.string.nfc_card_title_4k)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nameCard,
            style = LocalTypography.current.subtitleEB12,
            color = LocalPallet.current.onNfcCard
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_union),
            contentDescription = "",
            tint = LocalPallet.current.onNfcCard
        )
    }
}
