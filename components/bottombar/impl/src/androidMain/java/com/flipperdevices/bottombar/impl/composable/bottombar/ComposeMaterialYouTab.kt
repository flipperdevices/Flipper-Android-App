package com.flipperdevices.bottombar.impl.composable.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.ktx.tab.TabTransition
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposeMaterialYouTab(
    tabState: TabState,
    onClick: (() -> Unit),
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Box(
        modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.padding(top = 6.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    Modifier.size(width = 42.dp, height = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TabTransition(
                        activeColor = tabState.selectedColorIcon,
                        inactiveColor = tabState.unselectedColorIcon,
                        selected = selected
                    ) {
                        ComposableTabIcon(tabState, selected)
                    }
                }
                var indicationDotModifier = Modifier
                    .padding(bottom = 14.dp, end = 4.dp)
                    .size(12.dp)
                if (tabState.notificationDotActive) {
                    indicationDotModifier = indicationDotModifier
                        .clip(CircleShape)
                        .background(LocalPallet.current.updateProgressGreen)
                }
                Box(
                    modifier = indicationDotModifier
                )
            }
            Box {
                TabTransition(
                    activeColor = tabState.selectedColor,
                    inactiveColor = tabState.unselectedColor,
                    selected = selected
                ) {
                    ComposableStatusText(tabState)
                }
            }
        }
    }
}
