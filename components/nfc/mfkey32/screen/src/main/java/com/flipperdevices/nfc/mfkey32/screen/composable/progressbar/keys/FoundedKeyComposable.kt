package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.keys

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R

private const val GRID_WIDTH = 2

@Composable
fun FoundedKeyComposableGrid(modifier: Modifier, keys: List<String>) = Column {
    keys.windowed(GRID_WIDTH, GRID_WIDTH, partialWindows = true).forEach { rowKeys ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            rowKeys.forEach { key ->
                FoundedKeyComposable(modifier, key)
            }
        }
    }
}

@Composable
fun FoundedKeyComposable(
    modifier: Modifier = Modifier,
    key: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .padding(
                        start = 10.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 6.dp
                    )
                    .size(24.dp),
                painter = painterResource(R.drawable.pic_encrypted_key),
                contentDescription = key
            )
            SelectionContainer {
                Text(
                    modifier = Modifier.padding(
                        end = 12.dp
                    ),
                    text = key,
                    style = LocalTypography.current.monoSpaceM14,
                    color = LocalPallet.current.text100
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFoundedKeyComposablePreview() {
    FlipperThemeInternal {
        Box {
            FoundedKeyComposable(key = "A0B1C2D3A4A1")
        }
    }
}
