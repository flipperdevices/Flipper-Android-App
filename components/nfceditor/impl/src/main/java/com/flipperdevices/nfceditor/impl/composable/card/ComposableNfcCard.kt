package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfceditor.impl.model.NfcTypeCard

@Composable
fun ComposableNfcCard(type: NfcTypeCard) {
    Card(
        modifier = Modifier.padding(14.dp)
    ) {
        var isOpened by remember { mutableStateOf(true) }
        val onClick = { isOpened = !isOpened }
        BoxWithConstraints {
            Image(
                painter = painterResource(id = DesignSystem.drawable.nfc_card_back),
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )
            ComposableNfcCardInternal(
                type = type,
                isOpened = isOpened,
                onClick = onClick,
                height = this.maxHeight
            )
        }
    }
}

@Composable
private fun ComposableNfcCardInternal(
    type: NfcTypeCard,
    isOpened: Boolean,
    onClick: () -> Unit
) {
    Column(
        // modifier = Modifier.height(height),
        verticalArrangement = Arrangement.SpaceBetween
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
