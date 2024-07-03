package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants

@Suppress("LongMethod")
@Composable
fun NavigationButton(
    onUpClicked: () -> Unit,
    onRightClicked: () -> Unit,
    onDownClicked: () -> Unit,
    onLeftClicked: () -> Unit,
    onOkClicked: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color(0xFF303030),
    iconTint: Color = MaterialTheme.colors.onPrimary,
    textColor: Color = MaterialTheme.colors.onPrimary,
) {
    Box(
        modifier = modifier
            .size(GridConstants.DEFAULT_BUTTON_SIZE * 4)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = rememberVectorPainter(Icons.Filled.KeyboardArrowUp),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onUpClicked)
                .align(Alignment.TopCenter)
        )
        Icon(
            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowLeft),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onLeftClicked)
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .size(GridConstants.DEFAULT_BUTTON_SIZE)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primaryVariant)
                .padding(4.dp)
                .clip(CircleShape)
                .background(Color(0xFF616161))
                .clip(CircleShape)
                .clickable(onClick = onOkClicked)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center,
            content = {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.caption,
                    color = textColor,
                )
            }
        )

        Icon(
            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowRight),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onRightClicked)
                .align(Alignment.CenterEnd)
        )

        Icon(
            painter = rememberVectorPainter(Icons.Filled.KeyboardArrowDown),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onDownClicked)
                .align(Alignment.BottomCenter)
        )
    }
}
