package com.flipperdevices.selfupdater.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ApplicationParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

private const val DEBUG = "DEBUG"

class SelfUpdaterViewModel @VMInject constructor(
    private val applicationParams: ApplicationParams,
) : ViewModel() {
    private val isShowDialog = MutableStateFlow(false)
    fun dialogState() = isShowDialog.asStateFlow()

    init {
        processCheckUpdate()
    }

    fun cancelDialog() {
        viewModelScope.launch {
            isShowDialog.emit(false)
        }
    }

    private fun processCheckUpdate() {
        viewModelScope.launch {
            val currentAppVersion = applicationParams.version
            if (currentAppVersion == DEBUG) return@launch
        }
    }
}
