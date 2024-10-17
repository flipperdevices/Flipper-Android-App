package com.flipperdevices.ifrmvp.core.ui.button

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.ifrmvp.core.ui.button.core.ButtonClickEvent
import com.flipperdevices.ifrmvp.core.ui.button.core.ButtonPlaceholderBox
import com.flipperdevices.ifrmvp.core.ui.button.core.TextButton
import com.flipperdevices.ifrmvp.core.ui.button.core.buttonBackgroundColor
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf

@Composable
fun VerticalDoubleButton(
    onFirstClick: (ButtonClickEvent) -> Unit,
    onLastClick: (ButtonClickEvent) -> Unit,
    firstText: String,
    lastText: String,
    modifier: Modifier = Modifier,
    background: Color = buttonBackgroundColor,
    text: String? = null,
) {
    ButtonPlaceholderBox {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(8.sf))
                .background(background),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                onClick = onFirstClick,
                text = firstText,
                background = Color.Transparent,
            )
            text?.let {
                TextButton(
                    onClick = null,
                    text = text,
                    background = Color.Transparent,
                )
            }
            TextButton(
                onClick = onLastClick,
                text = lastText,
                background = Color.Transparent,
            )
        }
    }
}

@Composable
fun VolumeButton(
    onAddClick: (ButtonClickEvent) -> Unit,
    onReduceClick: (ButtonClickEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    VerticalDoubleButton(
        onFirstClick = onAddClick,
        onLastClick = onReduceClick,
        text = "VOL",
        firstText = "+",
        lastText = "-",
        modifier = modifier,
    )
}

@Composable
fun ChannelButton(
    onNextClick: (ButtonClickEvent) -> Unit,
    onPrevClick: (ButtonClickEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    VerticalDoubleButton(
        onFirstClick = onNextClick,
        onLastClick = onPrevClick,
        text = "CH",
        firstText = "+",
        lastText = "-",
        modifier = modifier,
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ChannelButtonPreview() {
    FlipperThemeInternal {
        Row(horizontalArrangement = Arrangement.spacedBy(4.sf)) {
            ChannelButton(
                onNextClick = {},
                onPrevClick = {}
            )
            VolumeButton(
                onReduceClick = {},
                onAddClick = {}
            )
        }
    }
}
