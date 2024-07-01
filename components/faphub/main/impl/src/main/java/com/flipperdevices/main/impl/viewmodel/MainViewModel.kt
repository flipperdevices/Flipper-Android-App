package com.flipperdevices.main.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.main.impl.model.FapHubTabEnum
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel @AssistedInject constructor(
    @Assisted deeplink: Deeplink.BottomBar.AppsTab.MainScreen?
) : DecomposeViewModel() {
    private val tabFlow = MutableStateFlow(
        when (deeplink) {
            Deeplink.BottomBar.AppsTab.MainScreen.InstalledTab -> FapHubTabEnum.INSTALLED
            null -> FapHubTabEnum.APPS
        }
    )

    fun getTabFlow(): StateFlow<FapHubTabEnum> = tabFlow

    fun onSelectTab(tabEnum: FapHubTabEnum) {
        viewModelScope.launch {
            tabFlow.emit(tabEnum)
        }
    }

    @AssistedFactory
    interface Factory {
        operator fun invoke(
            deeplink: Deeplink.BottomBar.AppsTab.MainScreen?
        ): MainViewModel
    }
}
