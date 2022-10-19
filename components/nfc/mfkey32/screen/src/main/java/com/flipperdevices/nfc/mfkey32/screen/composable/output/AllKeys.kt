package com.flipperdevices.nfc.mfkey32.screen.composable.output

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.titlecaseFirstCharIfItIsLowercase
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.nfc.mfkey32.screen.model.FoundedKey

fun LazyListScope.AllKeys(keys: List<FoundedKey>) {
    item(keys.size) {
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 14.dp, end = 14.dp, bottom = 10.dp),
            text = stringResource(R.string.mfkey32_founded_all_title, keys.size),
            style = LocalTypography.current.buttonM16,
            color = LocalPallet.current.text100
        )
    }

    items(items = keys) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTypography.current.subtitleM12.copy(
                fontWeight = FontWeight.W500,
                color = LocalPallet.current.text100
            )
        ) {
            ComposableFoundedKey(
                Modifier.padding(
                    start = 14.dp, end = 14.dp, top = 4.dp, bottom = 4.dp
                ), it
            )
        }
    }
}

private const val KEY_A = "A"
private const val KEY_B = "B"

@Composable
private fun ComposableFoundedKey(modifier: Modifier, foundedKey: FoundedKey) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append("Sector ")
            append(foundedKey.sectorName.titlecaseFirstCharIfItIsLowercase())
            append(" — ")
            val color = if (foundedKey.keyName.trim().equals(KEY_A, true)) {
                LocalPallet.current.nfcCardKeyAColor
            } else if (foundedKey.keyName.trim().equals(KEY_B, true)) {
                LocalPallet.current.nfcCardKeyBColor
            } else LocalTextStyle.current.color

            withStyle(
                LocalTextStyle.current.toSpanStyle().copy(
                    color = color
                )
            ) {
                append("Key ")
                append(foundedKey.keyName.uppercase())
            }
            append(" — ")
            append(foundedKey.key)
        })
}