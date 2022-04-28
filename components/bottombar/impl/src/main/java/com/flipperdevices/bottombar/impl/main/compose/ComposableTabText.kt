package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.composable.animatedDots

@Composable
fun ComposableStatusText(
    tabState: TabState
) {
    val originalText = stringResource(tabState.textId)
    var animatedText = originalText
    if (tabState.textDotsAnimated) {
        animatedText = originalText + animatedDots()
    }

    Text(
        text = animatedText,
        fontSize = 10.sp,
        fontWeight = FontWeight.W700
    )
}
