package com.flipperdevices.screenstreaming.impl.composable.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenSnapshot

@Composable
fun ComposableFlipperScreenWithOptions(
    flipperScreen: FlipperScreenSnapshot,
    onTakeScreenshot: () -> Unit,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier.padding(top = 14.dp, bottom = 24.dp),
    contentAlignment = Alignment.Center
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var isLock by remember { mutableStateOf(false) }
            ComposableFlipperScreenLock(
                isLock = isLock,
                onChangeState = { isLock = it }
            )
            ComposableFlipperScreenScreenshot(onClick = onTakeScreenshot)
        }

        ComposableFlipperScreen(flipperScreen.bitmap)
    }
}