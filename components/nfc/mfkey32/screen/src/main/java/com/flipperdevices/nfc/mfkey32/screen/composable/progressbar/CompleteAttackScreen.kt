package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.keys.FoundedKeyComposable

@Composable
fun CompleteAttack(
    keysCollected: List<String>,
    onDone: () -> Unit
) = Column(
    Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val text = if (keysCollected.size == 1) {
        stringResource(R.string.mfkey32_complete_title)
    } else stringResource(R.string.mfkey32_complete_multiple_title, keysCollected.size)
    Text(
        modifier = Modifier.padding(18.dp),
        text = text,
        style = LocalTypography.current.titleB18,
        color = LocalPallet.current.text100
    )
    Image(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 24.dp),
        painter = painterResource(
            if (MaterialTheme.colors.isLight) DesignSystem.drawable.pic_update_successfull
            else DesignSystem.drawable.pic_update_successfull_dark
        ),
        contentDescription = text
    )
    if (keysCollected.size == 1) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FoundedKeyComposable(key = keysCollected.first())
        }
    } else LazyVerticalGrid(
        columns = GridCells.Fixed(count = 2),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        items(keysCollected) { key ->
            Box(contentAlignment = Alignment.Center) {
                FoundedKeyComposable(modifier = Modifier.padding(4.dp), key)
            }
        }
    }

    ComposableFlipperButton(
        modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 24.dp)
            .fillMaxWidth(),
        text = stringResource(R.string.mfkey32_complete_btn),
        textPadding = PaddingValues(vertical = 14.dp),
        onClick = onDone
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun CompleteAttackPreviewSingle() {
    FlipperThemeInternal() {
        Box {
            CompleteAttack(listOf("A0B1C2D3A4A1")) {}
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun CompleteAttackPreviewEven() {
    FlipperThemeInternal() {
        Box {
            CompleteAttack(
                listOf(
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1"
                )
            ) {}
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun CompleteAttackPreviewOdd() {
    FlipperThemeInternal() {
        Box {
            CompleteAttack(
                listOf(
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1",
                    "A0B1C2D3A4A1"
                )
            ) {}
        }
    }
}