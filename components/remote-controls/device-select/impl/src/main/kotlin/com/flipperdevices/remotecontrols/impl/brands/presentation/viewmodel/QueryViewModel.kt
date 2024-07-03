package com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow

internal class QueryViewModel : DecomposeViewModel() {
    val query = MutableStateFlow("")

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun clearQuery() {
        query.value = ""
    }
}
