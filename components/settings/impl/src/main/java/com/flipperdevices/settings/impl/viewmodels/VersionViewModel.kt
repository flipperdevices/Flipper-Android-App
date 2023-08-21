package com.flipperdevices.settings.impl.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class VersionViewModel @VMInject constructor(
    private val selfUpdaterApi: SelfUpdaterApi,
    private val applicationParams: ApplicationParams
) : ViewModel(), LogTagProvider {
    override val TAG: String = "VersionViewModel"

    private val inProgressFlow = MutableStateFlow(false)
    fun inProgress() = inProgressFlow.asStateFlow()

    private val dialogFlow = MutableStateFlow(false)
    fun getDialogState() = dialogFlow.asStateFlow()

    fun versionApp() = applicationParams.version
    fun sourceInstall() = selfUpdaterApi.getInstallSourceName()

    fun isSelfUpdateManualChecked() = selfUpdaterApi.isSelfUpdateChecked()

    fun onCheckUpdates() {
        info { "#onCheckUpdates" }
        val activity = CurrentActivityHolder.getCurrentActivity()
        if (activity == null) {
            warn { "#onCheckUpdates activity null" }
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            inProgressFlow.emit(true)
            selfUpdaterApi.startCheckUpdate { result ->
                info { "#onCheckUpdates result: $result" }
                inProgressFlow.emit(false)

                if (result == SelfUpdateResult.NO_UPDATES) {
                    dialogFlow.emit(true)
                }
            }
        }
    }

    fun dismissDialog() {
        viewModelScope.launch(Dispatchers.Default) {
            dialogFlow.emit(false)
        }
    }
}
