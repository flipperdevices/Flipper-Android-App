package com.flipperdevices.core.ui.ktx.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.placeholderByLocalProvider
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableFlipperButton(
    text: String,
    modifier: Modifier = Modifier,
    textPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 38.dp),
    onClick: () -> Unit = {},
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val background by animateColorAsState(
        targetValue = if (enabled && !isLoading) {
            LocalPallet.current.accentSecond
        } else {
            LocalPallet.current.flipperDisableButton
        }
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(size = 30.dp))
            .placeholderByLocalProvider()
            .background(background)
            .clickableRipple(onClick = onClick, enabled = enabled && !isLoading),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isLoading,
            contentAlignment = Alignment.Center,
            content = { animatedIsLoading ->
                if (animatedIsLoading) {
                    CircularProgressIndicator(
                        color = LocalPalletV2.current.action.blue.icon.onColor,
                        modifier = Modifier.padding(textPadding).size(22.dp),
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(textPadding),
                        text = text,
                        color = LocalPallet.current.onFlipperButton,
                        style = LocalTypography.current.buttonB16.merge(textStyle)
                    )
                }
            }
        )
    }
}
