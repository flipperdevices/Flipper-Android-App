package com.flipperdevices.screenstreaming.impl.composable.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum

@Composable
fun ComposableFlipperControls(
    onPressButton: (ButtonEnum) -> Unit,
    onLongPressButton: (ButtonEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(30.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        ComposableFlipperDPad(
            onPressButton = onPressButton,
            onLongPressButton = onLongPressButton
        )
        ComposableFlipperBackButton(
            onPress = { onPressButton(ButtonEnum.BACK) },
            onLongPress = { onLongPressButton(ButtonEnum.BACK) }
        )
    }
}

@Composable
private fun ComposableFlipperBackButton(
    onPress: () -> Unit,
    onLongPress: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .border(
                width = 3.dp,
                color = LocalPallet.current.screenStreamingBorderColor,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
            .background(LocalPallet.current.accent)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { onPress() },
                onLongClick = { onLongPress() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_control_back),
            contentDescription = stringResource(R.string.control_back),
        )
    }
}
