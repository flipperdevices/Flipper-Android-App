package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.ktx.animatedDots
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableStatusText(
    tabState: TabState,
    modifier: Modifier = Modifier
) {
    val originalText = tabState.text
    var animatedText = originalText
    if (tabState.textDotsAnimated) {
        animatedText = originalText + animatedDots()
    }

    Text(
        modifier = modifier,
        text = animatedText,
        style = LocalTypography.current.subtitleB10
    )
}
