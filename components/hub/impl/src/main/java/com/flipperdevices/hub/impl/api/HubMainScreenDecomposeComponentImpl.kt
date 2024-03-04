package com.flipperdevices.hub.impl.api

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.faphub.maincard.api.MainCardApi
import com.flipperdevices.hub.impl.composable.ComposableHub
import com.flipperdevices.hub.impl.model.HubNavigationConfig
import com.flipperdevices.hub.impl.viewmodel.NfcAttackViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class HubMainScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<HubNavigationConfig>,
    private val mainCardApi: MainCardApi,
    private val metricApi: MetricApi,
    private val nfcAttackViewModelProvider: Provider<NfcAttackViewModel>
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val nfcAttackViewModel = viewModelWithFactory(key = null) {
            nfcAttackViewModelProvider.get()
        }
        val notificationCount by nfcAttackViewModel.getNfcAttackNotificationCountState()
            .collectAsState()
        val rootNavigation = LocalRootNavigation.current

        ComposableHub(
            notificationCount = notificationCount,
            onOpenAttack = {
                navigation.pushToFront(HubNavigationConfig.NfcAttack(null))
            },
            mainCardComposable = {
                mainCardApi.ComposableMainCard(
                    modifier = Modifier.padding(
                        start = 14.dp,
                        end = 14.dp,
                        top = 14.dp
                    ),
                    onClick = {
                        metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB)
                        navigation.pushToFront(HubNavigationConfig.FapHub(null))
                    },
                    componentContext = this
                )
            },
            onOpenRemoteControl = {
                rootNavigation.push(RootScreenConfig.ScreenStreaming)
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<HubNavigationConfig>
        ): HubMainScreenDecomposeComponentImpl
    }
}
