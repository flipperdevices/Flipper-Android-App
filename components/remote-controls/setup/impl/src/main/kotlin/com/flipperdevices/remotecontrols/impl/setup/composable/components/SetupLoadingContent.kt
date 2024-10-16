package com.flipperdevices.remotecontrols.impl.setup.composable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Composable
internal fun SetupLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .height(277.dp)
                    .fillMaxWidth()
                    .placeholderConnecting()
            )
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .width(12.dp)
                    .height(32.dp)
                    .placeholderConnecting()
            )
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .placeholderConnecting()
            )
        }

        Box(
            modifier = Modifier
                .padding(24.dp)
                .width(64.dp)
                .height(12.dp)
                .placeholderConnecting()
        )
    }
}

@Preview
@Composable
private fun SetupLoadingContentPreview() {
    FlipperThemeInternal {
        SetupLoadingContent()
    }
}
