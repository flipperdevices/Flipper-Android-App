package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableHeaderCard(
    nameCard: String,
    isOpened: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ComposableNameCard(nameCard)

        val imageId = if (isOpened) {
            R.drawable.ic_more
        } else R.drawable.ic_more_revert

        Icon(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
            painter = painterResource(id = imageId),
            contentDescription = "",
            tint = LocalPallet.current.onNfcCard
        )
    }
}

@Composable
private fun ComposableNameCard(nameCard: String) {
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
