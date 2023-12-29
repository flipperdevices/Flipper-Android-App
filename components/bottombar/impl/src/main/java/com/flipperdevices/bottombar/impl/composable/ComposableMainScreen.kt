package com.flipperdevices.bottombar.impl.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.router.stack.ChildStack
import com.flipperdevices.bottombar.impl.composable.bottombar.ComposeBottomBar
import com.flipperdevices.bottombar.impl.model.BottomBarTabConfig
import com.flipperdevices.bottombar.impl.model.BottomBarTabEnum
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.ktx.SetUpNavigationBarColor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.ui.decompose.DecomposeComponent

@Composable
@Suppress("NonSkippableComposable")
fun ComposableMainScreen(
    childStack: ChildStack<BottomBarTabConfig, DecomposeComponent>,
    connectionTabState: TabState,
    hubHasNotification: Boolean,
    onTabClick: (tab: BottomBarTabEnum, force: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedTab = childStack.active.configuration
    Scaffold(
        modifier = modifier,
        bottomBar = {
            ComposeBottomBar(
                connectionTabState = connectionTabState,
                selectedItem = selectedTab.enum,
                hubHasNotification = hubHasNotification,
                onBottomBarClick = {
                    onTabClick(it, selectedTab.enum == it)
                }
            )
            SetUpNavigationBarColor(color = LocalPallet.current.bottomBarBackground)
        }
    ) {
        Children(
            modifier = Modifier.padding(it),
            stack = childStack,
        ) {
            it.instance.Render()
        }
    }
}
