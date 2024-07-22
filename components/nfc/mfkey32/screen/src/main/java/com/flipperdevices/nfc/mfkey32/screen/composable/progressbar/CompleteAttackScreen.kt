package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.keys.FoundedKeyComposable
import com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.keys.FoundedKeyComposableGrid
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun CompleteAttack(
    keysCollected: ImmutableList<String>,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val text = if (keysCollected.size == 1) {
        stringResource(R.string.mfkey32_complete_title)
    } else {
        stringResource(R.string.mfkey32_complete_multiple_title, keysCollected.size)
    }
    Text(
        modifier = Modifier.padding(18.dp),
        text = text,
        style = LocalTypography.current.titleB18,
        color = LocalPallet.current.text100
    )
    Image(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 24.dp),
        painter = painterResource(
            if (MaterialTheme.colors.isLight) {
                DesignSystem.drawable.pic_update_successfull
            } else {
                DesignSystem.drawable.pic_update_successfull_dark
            }
        ),
        contentDescription = text
    )
    if (keysCollected.size == 1) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FoundedKeyComposable(key = keysCollected.first())
        }
    } else {
        FoundedKeyComposableGrid(
            modifier = Modifier.padding(6.dp),
            keys = keysCollected
        )
    }

    ComposableFlipperButton(
        modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 24.dp)
            .fillMaxWidth(),
        text = stringResource(R.string.mfkey32_complete_btn),
        textPadding = PaddingValues(vertical = 14.dp),
        onClick = onComplete
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableCompleteAttackPreviewSinglePreview() {
    FlipperThemeInternal {
        Box {
            CompleteAttack(persistentListOf("A0B1C2D3A4A1"), {})
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableCompleteAttackPreviewEvenPreview() {
    FlipperThemeInternal {
        Box {
            CompleteAttack(
                persistentListOf(
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1"
                ),
                {}
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableCompleteAttackPreviewOddPreview() {
    FlipperThemeInternal {
        Box {
            CompleteAttack(
                persistentListOf(
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1"
                ),
                {}
            )
        }
    }
}
