package com.flipperdevices.keyscreen.impl.composable.actions

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.ktx.onHoldPress
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.FlipperDeviceState
import com.flipperdevices.keyscreen.impl.viewmodel.FlipperDeviceViewModel

@Composable
fun ComposableSend(
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit = {}
) {
    val flipperDeviceViewModel = viewModel<FlipperDeviceViewModel>()
    val flipperDeviceState by flipperDeviceViewModel.getFlipperDeviceState().collectAsState()
    val enabled = flipperDeviceState == FlipperDeviceState.CONNECTED

    ComposableSendInternal(
        modifier = modifier,
        onClick = if (enabled) onClick else null
    )
}

@Composable
private fun ComposableSendInternal(
    modifier: Modifier = Modifier,
    onClick: ((Boolean) -> Unit)? = {}
) {
    val enable = onClick != null
    var isAction by remember { mutableStateOf(false) }
    var isNeedAnimation by remember { mutableStateOf(false) }

    val color = LocalPallet.current.accent
    var animColor = if (isAction) {
        val translateAnimation = rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1500f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = FastOutLinearInEasing
                )
            )
        )
        Brush.horizontalGradient(
            colors = listOf(color.copy(alpha = 0.9f), color.copy(alpha = 0.4f)),
            startX = 0f,
            endX = translateAnimation.value
        )
    } else SolidColor(color)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .height(49.dp).fillMaxWidth()
                .background(animColor)
                .border(
                    width = 2.dp,
                    color = if (enable) color else LocalPallet.current.text8,
                    shape = RoundedCornerShape(12.dp)
                )
                .onHoldPress(
                    onLongPressStart = {
                        isAction = true
                        onClick?.invoke(isAction)
                    },
                    onLongPressEnd = {
                        isAction = false
                        onClick?.invoke(isAction)
                    },
                    onClick = {

                    }
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = DesignSystem.drawable.ic_send),
                contentDescription = stringResource(id = R.string.keyscreen_send),
                tint = LocalPallet.current.onFlipperButton
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(id = R.string.keyscreen_send),
                style = LocalTypography.current.flipperAction,
                color = LocalPallet.current.onFlipperButton
            )
        }
        if (!isAction) {
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .height(11.dp),
                text = stringResource(id = R.string.keyscreen_sending_desc),
                style = LocalTypography.current.subtitleM12,
                color = LocalPallet.current.text20,
                textAlign = TextAlign.Center
            )
        } else {
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableSendPreview() {
    FlipperThemeInternal {
        Column(modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxSize()) {
            ComposableSendInternal()
        }
    }
}
