package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfceditor.impl.model.NfcTypeCard

@Composable
fun ComposableNfcCard(type: NfcTypeCard) {
    val brush = Brush.horizontalGradient(
        colors = listOf(
            LocalPallet.current.nfcCardBackground,
            LocalPallet.current.nfcCardBackgroundSecond
        )
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        var isOpened by remember { mutableStateOf(true) }
        val onClick = { isOpened = !isOpened }
        Column(
            Modifier.background(brush)
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTypography.current.monoSpaceM14
                    .copy(fontSize = 10.sp, color = LocalPallet.current.onNfcCard),
                LocalContentColor provides LocalPallet.current.onNfcCard
            ) {
                ComposableHeaderCard(type.nameType, isOpened, onClick)
                if (isOpened) {
                    ComposableSchemeCard()
                    ComposableAdditionalInfoCard(type)
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableNfcCardPreview() {
    FlipperThemeInternal {
        ComposableNfcCard(NfcTypeCard.Classic4k)
    }
}
