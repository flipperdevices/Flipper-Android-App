package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.nfceditor.impl.R

@Composable
fun ComposableSchemeCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.nfc_card_sector).uppercase(),
            fontSize = 6.sp,
            modifier = Modifier.rotate(-90f)
        )
        Icon(
            painter = painterResource(id = DesignSystem.drawable.ic_bracket),
            contentDescription = ""
        )
        ComposableSectorsCard()
    }
}

@Composable
private fun ComposableSectorsCard() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ComposableFirstSector()
        ComposableWhiteSector()
        ComposableWhiteSector()
        ComposableSecondSector()
    }
}

@Composable
private fun ComposableSector(
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(id = R.string.nfc_card_block).uppercase(),
            fontSize = 6.sp
        )
        Row(modifier = Modifier.weight(16f)) {
            content()
        }
    }
}

@Composable
private fun ComposableFirstSector() {
    val text = "${stringResource(id = R.string.nfc_card_uid)} " +
        "+ ${stringResource(id = R.string.nfc_card_manufacture_data).uppercase()}"

    ComposableSector {
        repeat(3) {
            Text(
                modifier = Modifier.weight(1f),
                text = "00",
                style = LocalTextStyle.current
                    .copy(color = LocalPallet.current.purpleNfc.copy(alpha = 0.6f)),
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier.weight(10f),
            text = text,
            style = LocalTextStyle.current.copy(
                color = LocalPallet.current.purpleNfc,
                fontWeight = FontWeight.W700
            ),
            textAlign = TextAlign.Center
        )
        repeat(3) {
            Text(
                modifier = Modifier.weight(1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.purpleNfc.copy(alpha = 0.6f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ComposableWhiteSector() {
    ComposableSector {
        repeat(16) {
            Text(
                modifier = Modifier.weight(1f),
                text = "00",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ComposableSecondSector() {
    ComposableSector {
        repeat(2) {
            Text(
                modifier = Modifier.weight(1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.greenNfc.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier.weight(2f),
            text = stringResource(id = R.string.nfc_card_key_a).uppercase(),
            style = LocalTextStyle.current.copy(
                color = LocalPallet.current.greenNfc,
                fontWeight = FontWeight.W700
            ),
            textAlign = TextAlign.Center
        )

        repeat(2) {
            Text(
                modifier = Modifier.weight(1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.greenNfc.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center
            )
        }

        Text(
            modifier = Modifier.weight(weight = 4f),
            text = stringResource(id = R.string.nfc_card_acess_bits).uppercase(),
            style = LocalTextStyle.current.copy(
                color = LocalPallet.current.redNfc,
                fontWeight = FontWeight.W700
            ),
            textAlign = TextAlign.Center
        )
        repeat(2) {
            Text(
                modifier = Modifier.weight(1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.blueNfc.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier.weight(2f),
            text = stringResource(id = R.string.nfc_card_key_b).uppercase(),
            style = LocalTextStyle.current.copy(
                color = LocalPallet.current.blueNfc,
                fontWeight = FontWeight.W700
            ),
            textAlign = TextAlign.Center
        )
        repeat(2) {
            Text(
                modifier = Modifier.weight(1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.blueNfc.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
