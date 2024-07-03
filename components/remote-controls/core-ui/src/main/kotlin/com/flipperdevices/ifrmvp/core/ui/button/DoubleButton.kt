package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.ifrmvp.core.ui.button.core.TextButton

@Composable
fun DoubleButton(
    onFirstClicked: () -> Unit,
    onLastClicked: () -> Unit,
    text: String? = null,
    firstText: String,
    lastText: String
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF303030)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = onFirstClicked,
            text = firstText,
            background = Color(0xFF303030),
            fontSize = 24.sp
        )
        text?.let {
            TextButton(
                onClick = null,
                text = text,
                background = Color(0xFF303030)
            )
        }
        TextButton(
            onClick = onLastClicked,
            text = lastText,
            background = Color(0xFF303030),
            fontSize = 24.sp
        )
    }
}

@Composable
fun VolumeButton(
    onAddClicked: () -> Unit,
    onReduceClicked: () -> Unit
) {
    DoubleButton(
        onFirstClicked = onAddClicked,
        onLastClicked = onReduceClicked,
        text = "VOL",
        firstText = "+",
        lastText = "-"
    )
}

@Composable
fun ChannelButton(
    onNextClicked: () -> Unit,
    onPrevClicked: () -> Unit
) {
    DoubleButton(
        onFirstClicked = onNextClicked,
        onLastClicked = onPrevClicked,
        text = "CH",
        firstText = "+",
        lastText = "-"
    )
}
