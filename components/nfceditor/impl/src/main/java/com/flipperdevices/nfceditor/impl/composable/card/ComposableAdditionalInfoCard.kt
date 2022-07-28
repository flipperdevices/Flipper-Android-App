package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.nfceditor.impl.R
import com.flipperdevices.nfceditor.impl.model.NfcEditorCardInfo

@Composable
fun ComposableAdditionalInfoCard(nfcEditorCardInfo: NfcEditorCardInfo) {
    Column(modifier = Modifier.padding(12.dp)) {
        if (nfcEditorCardInfo.uid != null) {
            Row {
                Text(
                    nameValueAnnotatedString(
                        name = stringResource(R.string.nfc_card_uid),
                        value = nfcEditorCardInfo.uid
                    )
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (nfcEditorCardInfo.atqa != null) {
                Text(
                    nameValueAnnotatedString(
                        name = stringResource(R.string.nfc_card_atqa),
                        value = nfcEditorCardInfo.atqa
                    )
                )
            }
            if (nfcEditorCardInfo.sak != null) {
                Text(
                    nameValueAnnotatedString(
                        name = stringResource(R.string.nfc_card_sak),
                        value = nfcEditorCardInfo.sak
                    )
                )
            }
        }
    }
}

@Composable
private fun nameValueAnnotatedString(name: String, value: String): AnnotatedString {
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
