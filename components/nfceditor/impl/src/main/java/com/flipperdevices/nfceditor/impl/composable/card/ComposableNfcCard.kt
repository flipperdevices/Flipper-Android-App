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
import com.flipperdevices.core.data.PredefinedEnumMap
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfceditor.impl.R
import com.flipperdevices.nfceditor.impl.model.CardFieldInfo
import com.flipperdevices.nfceditor.impl.model.NfcCellType
import com.flipperdevices.nfceditor.impl.model.NfcEditorCardInfo
import com.flipperdevices.nfceditor.impl.model.NfcEditorCardType
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ComposableNfcCard(
    nfcEditorCardInfo: NfcEditorCardInfo,
    scaleFactor: Float,
    currentCell: NfcEditorCellLocation?,
    onCellClick: (NfcEditorCellLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(top = 14.dp, bottom = 14.dp, start = 14.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = LocalPallet.current.nfcCardBackground
    ) {
        Box {
            var isOpened by remember { mutableStateOf(true) }
            val onClick = { isOpened = !isOpened }
            if (isOpened) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(R.drawable.pic_nfccard),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
            ComposableNfcCardInternal(
                modifier = if (isOpened) {
                    Modifier.matchParentSize()
                } else {
                    Modifier
                },
                scaleFactor = scaleFactor,
                nfcEditorCardInfo = nfcEditorCardInfo,
                isOpened = isOpened,
                onClick = onClick,
                currentCell = currentCell,
                onCellClick = onCellClick
            )
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun ComposableNfcCardInternal(
    nfcEditorCardInfo: NfcEditorCardInfo,
    isOpened: Boolean,
    onClick: () -> Unit,
    scaleFactor: Float,
    currentCell: NfcEditorCellLocation?,
    modifier: Modifier = Modifier,
    onCellClick: (NfcEditorCellLocation) -> Unit
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
                ComposableAdditionalInfoCard(
                    nfcEditorCardInfo,
                    currentCell,
                    scaleFactor,
                    onCellClick
                )
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
            nfcEditorCardInfo = NfcEditorCardInfo(
                cardType = NfcEditorCardType.MF_4K,
                PredefinedEnumMap(CardFieldInfo::class.java) { _ ->
                    "B6 69 03 36 8A 98 02".split(" ")
                        .map { content -> NfcEditorCell(content, NfcCellType.SIMPLE) }
                        .toImmutableList()
                }
            ),
            scaleFactor = 1.0f,
            currentCell = null,
            onCellClick = {}
        )
    }
}
