package com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class QueryViewModel @Inject constructor() : DecomposeViewModel() {
    val query = MutableStateFlow("")

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun clearQuery() {
        query.value = ""
    }
}
