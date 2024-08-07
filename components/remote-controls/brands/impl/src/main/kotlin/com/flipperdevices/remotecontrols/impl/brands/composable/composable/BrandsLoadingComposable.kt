package com.flipperdevices.remotecontrols.impl.brands.composable.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2

private fun LazyListScope.itemsPlaceholder(count: Int) {
    item {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .placeholderConnecting()
        )
    }
    item {
        Spacer(Modifier.height(12.dp))
    }
    items(count) {
        Box(
            modifier = Modifier
                .width(256.dp)
                .height(20.dp)
                .placeholderConnecting()
        )
        Spacer(Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LocalPalletV2.current.surface.backgroundMain.separator)
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun BrandsLoadingComposable(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(end = 14.dp)
        ) {
            itemsPlaceholder(count = 4)
            itemsPlaceholder(count = 8)
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            repeat(times = 24) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .width(16.dp)
                        .height(16.dp)
                        .placeholderConnecting()
                )
            }
        }
    }
}

@Preview
@Composable
private fun BrandsLoadingComposablePreview() {
    FlipperThemeInternal {
        BrandsLoadingComposable()
    }
}
