package com.flipperdevices.bottombar.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.router.stack.ChildStack
import com.flipperdevices.bottombar.impl.composable.bottombar.ComposeBottomBar
import com.flipperdevices.bottombar.impl.model.BottomBarTabConfig
import com.flipperdevices.bottombar.impl.model.BottomBarTabEnum
import com.flipperdevices.bottombar.impl.viewmodel.InAppNotificationViewModel
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.flipperdevices.ui.decompose.DecomposeComponent

@Composable
@Suppress("NonSkippableComposable")
fun ComposableMainScreen(
    notificationViewModel: InAppNotificationViewModel,
    notificationRenderer: InAppNotificationRenderer,
    childStack: ChildStack<BottomBarTabConfig, DecomposeComponent>,
    connectionTabState: TabState,
    toolsHasNotification: Boolean,
    appsHasNotification: Boolean,
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
                toolsHasNotification = toolsHasNotification,
                appsHasNotification = appsHasNotification,
                onBottomBarClick = {
                    onTabClick(it, selectedTab.enum == it)
                }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Children(
                stack = childStack,
            ) {
                it.instance.Render()
            }

            val notificationState by notificationViewModel.state().collectAsState()

            ComposableInAppNotification(
                modifier = Modifier.align(Alignment.BottomCenter),
                notificationRenderer = notificationRenderer,
                notificationState = notificationState,
                onNotificationHidden = notificationViewModel::onNotificationHidden
            )
        }
    }
}
