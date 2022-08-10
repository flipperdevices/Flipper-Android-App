package com.flipperdevices.nfceditor.impl.composable.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.flipperdevices.nfceditor.impl.model.NfcEditorCardInfo
import com.flipperdevices.nfceditor.impl.model.NfcEditorCardType

@Composable
fun ComposableNfcCard(
    nfcEditorCardInfo: NfcEditorCardInfo?,
    scaleFactor: Float
) {
    if (nfcEditorCardInfo == null) {
        return
    }
    Card(
        modifier = Modifier.padding(top = 14.dp, bottom = 14.dp, start = 14.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = LocalPallet.current.nfcCardBackground
    ) {
        Box {
            var isOpened by remember { mutableStateOf(true) }
            val onClick = { isOpened = !isOpened }
            if (isOpened) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(DesignSystem.drawable.pic_nfccard),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
            ComposableNfcCardInternal(
                modifier = if (isOpened) {
                    Modifier.matchParentSize()
                } else Modifier,
                scaleFactor = scaleFactor,
                nfcEditorCardInfo = nfcEditorCardInfo,
                isOpened = isOpened,
                onClick = onClick
            )
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun ComposableNfcCardInternal(
    modifier: Modifier,
    nfcEditorCardInfo: NfcEditorCardInfo,
    isOpened: Boolean,
    onClick: () -> Unit,
    scaleFactor: Float
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTypography.current.monoSpaceM14
                .copy(fontSize = (scaleFactor * 10).sp, color = LocalPallet.current.onNfcCard),
            LocalContentColor provides LocalPallet.current.onNfcCard
        ) {
            ComposableHeaderCard(nfcEditorCardInfo.cardType, isOpened, onClick)
            if (isOpened) {
                ComposableSchemeCard(scaleFactor)
                ComposableAdditionalInfoCard(nfcEditorCardInfo)
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
        ComposableNfcCard(
            NfcEditorCardInfo(
                cardType = NfcEditorCardType.MF_4K,
                uid = "B6 69 03 36 8A 98 02",
                atqa = "02 02",
                sak = "98"
            ),
            scaleFactor = 1.0f
        )
    }
}
