package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(id = R.string.nfc_card_block).uppercase(),
            fontSize = 6.sp
        )
        content()
    }
}

@Composable
private fun ComposableFirstSector() {
    ComposableSector {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.purpleNfc.copy(alpha = 0.5f)
                    ).toSpanStyle()
                ) {
                    append("00 00 00")
                }
                append(" ")
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.purpleNfc,
                        fontWeight = FontWeight.W700
                    ).toSpanStyle()
                ) {
                    append("UID + MANUFACTURING DATA")
                }
                append(" ")
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.purpleNfc
                    ).toSpanStyle()
                ) {
                    append("00 00 00")
                }
            }
        )
    }
}

@Composable
private fun ComposableWhiteSector() {
    ComposableSector {
        Text(
            text = buildAnnotatedString {
                append("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00")
            }
        )
    }
}

@Composable
private fun ComposableSecondSector() {
    ComposableSector {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.greenNfc.copy(alpha = 0.5f)
                    ).toSpanStyle()
                ) {
                    append("00 00")
                }
                append(" ")
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.greenNfc,
                        fontWeight = FontWeight.W700
                    ).toSpanStyle()
                ) {
                    append("KEY A")
                }
                append(" ")
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.greenNfc.copy(alpha = 0.5f)
                    ).toSpanStyle()
                ) {
                    append("00 00")
                }
                append(" ")
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.redNfc,
                        fontWeight = FontWeight.W700
                    ).toSpanStyle()
                ) {
                    append("ACCESS BITS")
                }
                append(" ")
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.blueNfc.copy(alpha = 0.5f)
                    ).toSpanStyle()
                ) {
                    append("00 00")
                }
                append(" ")
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.blueNfc,
                        fontWeight = FontWeight.W700
                    ).toSpanStyle()
                ) {
                    append("KEY B")
                }
                append(" ")
                withStyle(
                    style = LocalTextStyle.current.copy(
                        color = LocalPallet.current.blueNfc.copy(alpha = 0.5f)
                    ).toSpanStyle()
                ) {
                    append("00 00")
                }
            }
        )
    }
}
