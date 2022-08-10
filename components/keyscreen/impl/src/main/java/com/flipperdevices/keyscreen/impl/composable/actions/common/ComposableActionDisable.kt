package com.flipperdevices.keyscreen.impl.composable.actions.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.impl.R

@Composable
fun ComposableActionDisable(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    @StringRes textId: Int
) {
    val color = LocalPallet.current.text8
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .height(49.dp)
                .fillMaxWidth()
                .background(color)
                .border(
                    width = 2.dp,
                    color = color,
                    shape = RoundedCornerShape(12.dp)
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComposableActionFlipperContent(
                iconId = iconId,
                textId = textId
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableComposableDisableActionPreview() {
    FlipperThemeInternal {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            ComposableActionDisable(
                textId = R.string.keyscreen_emulate,
                iconId = DesignSystem.drawable.ic_emulate
            )
            ComposableActionDisable(
                textId = R.string.keyscreen_send,
                iconId = DesignSystem.drawable.ic_send
            )
        }
    }
}
