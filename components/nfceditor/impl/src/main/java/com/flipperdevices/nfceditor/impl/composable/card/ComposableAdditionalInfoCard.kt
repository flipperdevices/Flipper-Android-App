package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.nfceditor.impl.model.NfcTypeCard

@Composable
fun ComposableAdditionalInfoCard(type: NfcTypeCard) {
    Column(modifier = Modifier.padding(12.dp)) {
        Row {
            Text(text = buildAnnotatedString("UID:", type.UID))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(text = buildAnnotatedString("ATQA:", type.ATQA))
            Text(text = buildAnnotatedString("SAK:", type.SAK))
        }
    }
}

@Composable
private fun buildAnnotatedString(name: String, value: String): AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        val style = LocalTextStyle.current
        withStyle(
            style = style.copy(
                fontWeight = FontWeight.W700
            ).toSpanStyle()
        ) {
            append(name)
        }
        append(" ")
        withStyle(
            style = style.toSpanStyle()
        ) {
            append(value)
        }
    }
}
