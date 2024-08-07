package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.SyncingBox
import com.flipperdevices.ifrmvp.core.ui.button.core.TextButton
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf

@Composable
fun DoubleButton(
    onFirstClick: () -> Unit,
    onLastClick: () -> Unit,
    firstText: String,
    lastText: String,
    isEmulating: Boolean,
    isSyncing: Boolean,
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.sf))
            .background(LocalPalletV2.current.surface.menu.body.dufault),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = onFirstClick,
            text = firstText,
            background = LocalPalletV2.current.surface.menu.body.dufault,
            isEmulating = isEmulating,
            isSyncing = false
        )
        text?.let {
            TextButton(
                onClick = null,
                text = text,
                background = LocalPalletV2.current.surface.menu.body.dufault,
                isEmulating = isEmulating,
                isSyncing = false
            )
        }
        TextButton(
            onClick = onLastClick,
            text = lastText,
            background = LocalPalletV2.current.surface.menu.body.dufault,
            isEmulating = isEmulating,
            isSyncing = false
        )
        SyncingBox(isSyncing = isSyncing)
    }
}

@Composable
fun VolumeButton(
    onAddClick: () -> Unit,
    onReduceClick: () -> Unit,
    isEmulating: Boolean,
    isSyncing: Boolean,
    modifier: Modifier = Modifier
) {
    DoubleButton(
        onFirstClick = onAddClick,
        onLastClick = onReduceClick,
        text = "VOL",
        firstText = "+",
        lastText = "-",
        modifier = modifier,
        isEmulating = isEmulating,
        isSyncing = isSyncing
    )
}

@Composable
fun ChannelButton(
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    isEmulating: Boolean,
    isSyncing: Boolean,
    modifier: Modifier = Modifier,
) {
    DoubleButton(
        onFirstClick = onNextClick,
        onLastClick = onPrevClick,
        text = "CH",
        firstText = "+",
        lastText = "-",
        modifier = modifier,
        isEmulating = isEmulating,
        isSyncing = isSyncing
    )
}
