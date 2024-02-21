package com.flipperdevices.settings.impl.viewmodels

import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class VersionViewModel @Inject constructor(
    private val selfUpdaterApi: SelfUpdaterApi,
    private val applicationParams: ApplicationParams
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "VersionViewModel"

    fun inProgress() = selfUpdaterApi.getInProgressState()

    private val dialogFlow = MutableStateFlow(false)
    fun getDialogState() = dialogFlow.asStateFlow()

    fun versionApp() = applicationParams.version
    fun sourceInstall() = selfUpdaterApi.getInstallSourceName()

    fun isSelfUpdateManualChecked() = selfUpdaterApi.isSelfUpdateCanManualCheck()

    fun onCheckUpdates() {
        info { "#onCheckUpdates" }
        viewModelScope.launch(Dispatchers.Default) {
            val result = selfUpdaterApi.startCheckUpdate(manual = true)
            info { "#onCheckUpdates result: $result" }
            when (result) {
                SelfUpdateResult.NO_UPDATES -> dialogFlow.emit(true)
                else -> {}
            }
        }
    }

    fun dismissDialog() {
        viewModelScope.launch(Dispatchers.Default) {
            dialogFlow.emit(false)
        }
    }
}
