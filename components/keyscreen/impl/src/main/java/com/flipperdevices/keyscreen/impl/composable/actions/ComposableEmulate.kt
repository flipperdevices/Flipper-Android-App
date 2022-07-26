package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.BrushPainter
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.FlipperDeviceState
import com.flipperdevices.keyscreen.impl.viewmodel.FlipperDeviceViewModel

@Composable
fun ComposableEmulate(
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit = {}
) {
    val flipperDeviceViewModel = viewModel<FlipperDeviceViewModel>()
    val flipperDeviceState by flipperDeviceViewModel.getFlipperDeviceState().collectAsState()
    val enabled = flipperDeviceState == FlipperDeviceState.CONNECTED

    ComposableEmulateInternal(
        modifier = modifier,
        onClick = if (enabled) onClick else null
    )
}

@Composable
private fun ComposableEmulateInternal(
    modifier: Modifier = Modifier,
    onClick: ((Boolean) -> Unit)? = {}
) {
//    val enable = onClick != null
//    var isAction by remember { mutableStateOf(false) }
//    val textId = if (isAction) R.string.keyscreen_emulating else R.string.keyscreen_emulate
//
//    val animColor = Brush.linearGradient(
//        colors = listOf(Color.Yellow, Color.Red),
//        start = Offset(0f, Float.POSITIVE_INFINITY),
//        end = Offset(Float.POSITIVE_INFINITY, 0f)
//    )
//
//    Button(
//        onClick = {
//            isAction = !isAction
//            onClick?.invoke(isAction)
//        },
//        colors = buttonColors(
//            backgroundColor = animColor,
//            contentColor = LocalPallet.current.onFlipperButton,
//            disabledBackgroundColor = LocalPallet.current.text8,
//            disabledContentColor = LocalPallet.current.onFlipperButton,
//        ),
//        enabled = enable,
//        modifier = modifier.height(49.dp),
//        shape = RoundedCornerShape(12.dp),
//        border = BorderStroke(
//            width = 2.dp,
//            color = if(enable) LocalPallet.current.accentSecond else LocalPallet.current.text8
//        ),
//        elevation = null
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                painter = painterResource(id = DesignSystem.drawable.ic_emulate),
//                contentDescription = "Emulate"
//            )
//            Spacer(Modifier.width(13.dp))
//            Text(
//                text = stringResource(id = textId),
//                style = LocalTypography.current.flipperAction
//            )
//        }
//    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableEmulatePreview() {
    FlipperThemeInternal {
        Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxSize()) {
            ComposableEmulateInternal()
            ComposableEmulateInternal(onClick = null)
        }
    }
}
