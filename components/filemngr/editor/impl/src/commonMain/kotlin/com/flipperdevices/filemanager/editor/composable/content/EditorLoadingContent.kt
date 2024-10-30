package com.flipperdevices.filemanager.editor.composable.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import kotlin.random.Random

@Composable
fun EditorLoadingContent(modifier: Modifier = Modifier) {
    val widths = remember(Unit) {
        List(
            size = 32,
            init = { Random.nextDouble(from = 0.4, until = 0.9).toFloat() }
        )
    }
    Box(modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            widths.forEach { width ->
                Box(
                    modifier = Modifier.height(12.dp)
                        .fillMaxWidth(width)
                        .placeholderConnecting()
                )
            }
        }
    }
}
