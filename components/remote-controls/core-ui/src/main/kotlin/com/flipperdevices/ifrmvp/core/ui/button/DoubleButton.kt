package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.TextButton

@Composable
fun DoubleButton(
    onFirstClicked: () -> Unit,
    onLastClicked: () -> Unit,
    firstText: String,
    lastText: String,
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LocalPalletV2.current.surface.menu.body.dufault),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = onFirstClicked,
            text = firstText,
            background = LocalPalletV2.current.surface.menu.body.dufault,
            fontSize = 24.sp
        )
        text?.let {
            TextButton(
                onClick = null,
                text = text,
                background = LocalPalletV2.current.surface.menu.body.dufault
            )
        }
        TextButton(
            onClick = onLastClicked,
            text = lastText,
            background = LocalPalletV2.current.surface.menu.body.dufault,
            fontSize = 24.sp
        )
    }
}

@Composable
fun VolumeButton(
    onAddClicked: () -> Unit,
    onReduceClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    DoubleButton(
        onFirstClicked = onAddClicked,
        onLastClicked = onReduceClicked,
        text = "VOL",
        firstText = "+",
        lastText = "-",
        modifier = modifier
    )
}

@Composable
fun ChannelButton(
    onNextClicked: () -> Unit,
    onPrevClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DoubleButton(
        onFirstClicked = onNextClicked,
        onLastClicked = onPrevClicked,
        text = "CH",
        firstText = "+",
        lastText = "-",
        modifier = modifier
    )
}
