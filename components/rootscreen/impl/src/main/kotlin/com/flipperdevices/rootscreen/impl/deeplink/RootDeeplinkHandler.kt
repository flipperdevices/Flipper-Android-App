package com.flipperdevices.rootscreen.impl.deeplink

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bottombar.api.BottomBarDecomposeComponent
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.rootscreen.api.RootDeeplinkHandler
import com.flipperdevices.rootscreen.model.RootScreenConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.findComponentByConfig

class RootDeeplinkHandler(
    private val navigation: StackNavigation<RootScreenConfig>,
    private val stack: Value<ChildStack<RootScreenConfig, DecomposeComponent>>,
    private val firstPairApi: FirstPairApi
) : RootDeeplinkHandler, LogTagProvider {
    override val TAG = "RootDeeplinkHandler"

    override fun handleDeeplink(deeplink: Deeplink) {
        when (deeplink) {
            is Deeplink.RootLevel -> navigation.push(getConfigFromRootLevelDeeplink(deeplink))
            is Deeplink.BottomBar -> {
                if (firstPairApi.shouldWeOpenPairScreen()) {
                    navigation.bringToFront(RootScreenConfig.FirstPair(deeplink))
                    return
                }
                val component = stack.findComponentByConfig(RootScreenConfig.BottomBar::class)
                if (component == null || component !is BottomBarDecomposeComponent) {
                    warn { "Bottom bar component is not exist in stack, but first pair screen already passed" }
                    navigation.bringToFront(RootScreenConfig.BottomBar(deeplink))
                    return
                }
                component.handleDeeplink(deeplink)
            }
        }
    }

    companion object {
        fun getConfigStackFromDeeplink(deeplink: Deeplink?): List<RootScreenConfig> {
            return when (deeplink) {
                is Deeplink.BottomBar -> listOf(RootScreenConfig.BottomBar(deeplink))
                is Deeplink.RootLevel -> listOf(
                    RootScreenConfig.BottomBar(null),
                    getConfigFromRootLevelDeeplink(deeplink)
                )

                null -> listOf(RootScreenConfig.BottomBar(null))
            }
        }

        private fun getConfigFromRootLevelDeeplink(deeplink: Deeplink.RootLevel): RootScreenConfig {
            return when (deeplink) {
                is Deeplink.RootLevel.SaveKey -> RootScreenConfig.SaveKey(deeplink)
                is Deeplink.RootLevel.WidgetOptions -> RootScreenConfig.WidgetOptions(deeplink.appWidgetId)
            }
        }
    }

}