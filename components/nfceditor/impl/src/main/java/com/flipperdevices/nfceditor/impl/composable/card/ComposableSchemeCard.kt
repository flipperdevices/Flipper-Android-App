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
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.nfceditor.impl.R

@Composable
@Suppress("MagicNumber")
fun ComposableSchemeCard(
    scaleFactor: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding((scaleFactor * 10).dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.nfc_card_sector).uppercase(),
            fontSize = (scaleFactor * 6).sp,
            modifier = Modifier.rotate(degrees = -90f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_bracket),
            contentDescription = ""
        )
        ComposableSectorsCard(scaleFactor)
    }
}

@Composable
private fun ComposableSectorsCard(scaleFactor: Float) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ComposableFirstSector(scaleFactor)
        ComposableWhiteSector(scaleFactor)
        ComposableWhiteSector(scaleFactor)
        ComposableSecondSector(scaleFactor)
    }
}

@Composable
@Suppress("MagicNumber")
private fun ComposableSector(
    scaleFactor: Float,
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.nfc_card_block).uppercase(),
            fontSize = (scaleFactor * 6).sp
        )
        Row(
            modifier = Modifier.weight(weight = 16f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
private fun ComposableFirstSector(scaleFactor: Float) {
    val text = "${stringResource(id = R.string.nfc_card_uid)} " +
        "+ ${stringResource(id = R.string.nfc_card_manufacture_data).uppercase()}"

    ComposableSector(scaleFactor) {
        repeat(times = 3) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = "00",
                style = LocalTextStyle.current
                    .copy(color = LocalPallet.current.nfcCardUIDColor.copy(alpha = 0.6f)),
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier.weight(weight = 10f),
            text = text,
            style = LocalTextStyle.current.copy(
                color = LocalPallet.current.nfcCardUIDColor,
                fontWeight = FontWeight.W700
            ),
            textAlign = TextAlign.Center
        )
        repeat(times = 3) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.nfcCardUIDColor.copy(alpha = 0.6f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ComposableWhiteSector(scaleFactor: Float) {
    ComposableSector(scaleFactor) {
        repeat(times = 16) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = "00",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun ComposableSecondSector(scaleFactor: Float) {
    ComposableSector(scaleFactor) {
        repeat(2) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.nfcCardKeyAColor.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier.weight(weight = 2f),
            text = stringResource(id = R.string.nfc_card_key_a).uppercase(),
            style = LocalTextStyle.current.copy(
                color = LocalPallet.current.nfcCardKeyAColor,
                fontWeight = FontWeight.W700
            ),
            textAlign = TextAlign.Center
        )

        repeat(2) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.nfcCardKeyAColor.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center
            )
        }

        Text(
            modifier = Modifier.weight(weight = 4f),
            text = stringResource(id = R.string.nfc_card_acess_bits).uppercase(),
            style = LocalTextStyle.current.copy(
                color = LocalPallet.current.nfcCardAccessBitsColor,
                fontWeight = FontWeight.W700
            ),
            textAlign = TextAlign.Center
        )
        repeat(2) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.nfcCardKeyBColor.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier.weight(weight = 2f),
            text = stringResource(id = R.string.nfc_card_key_b).uppercase(),
            style = LocalTextStyle.current.copy(
                color = LocalPallet.current.nfcCardKeyBColor,
                fontWeight = FontWeight.W700
            ),
            textAlign = TextAlign.Center
        )
        repeat(2) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = "00",
                style = LocalTextStyle.current.copy(
                    color = LocalPallet.current.nfcCardKeyBColor.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
