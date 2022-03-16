package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bottombar.model.TabState

@Suppress("LongParameterList")
@Composable
fun ComposeMaterialYouTab(
    tabState: TabState,
    selected: Boolean = false,
    onClick: (() -> Unit)
) {
    Box(
        Modifier.clickable(
            indication = null,
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() }
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(width = 42.dp, height = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                TabTransition(
                    activeColor = colorResource(tabState.selectedColorIcon),
                    inactiveColor = colorResource(tabState.unselectedColorIcon),
                    selected = selected
                ) {
                    ComposableTabIcon(tabState, selected)
                }
            }
            Box {
                TabTransition(
                    activeColor = colorResource(tabState.selectedColor),
                    inactiveColor = colorResource(tabState.unselectedColor),
                    selected = selected
                ) {
                    ComposableStatusText(tabState)
                }
            }
        }
    }
}
