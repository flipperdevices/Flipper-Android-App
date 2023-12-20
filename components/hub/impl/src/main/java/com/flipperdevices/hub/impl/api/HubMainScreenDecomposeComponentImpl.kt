package com.flipperdevices.hub.impl.api

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.faphub.maincard.api.MainCardApi
import com.flipperdevices.hub.impl.composable.ComposableHub
import com.flipperdevices.hub.impl.model.HubNavigationConfig
import com.flipperdevices.hub.impl.viewmodel.NfcAttackViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class HubMainScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<HubNavigationConfig>,
    private val mainCardApi: MainCardApi,
    private val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
    private val metricApi: MetricApi,
    private val nfcAttackViewModelProvider: Provider<NfcAttackViewModel>
) : DecomposeComponent, ComponentContext by componentContext {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val globalNavController = LocalGlobalNavigationNavStack.current

        val nfcAttackViewModel = viewModelWithFactory(key = null) {
            nfcAttackViewModelProvider.get()
        }
        val notificationCount by nfcAttackViewModel.getNfcAttackNotificationCountState()
            .collectAsState()

        ComposableHub(
            notificationCount = notificationCount,
            onOpenAttack = {
                navigation.push(HubNavigationConfig.NfcAttack)
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
                        navigation.push(HubNavigationConfig.FapHub)
                    }
                )
            },
            onOpenRemoteControl = {
                globalNavController.navigate(screenStreamingFeatureEntry.start())
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
