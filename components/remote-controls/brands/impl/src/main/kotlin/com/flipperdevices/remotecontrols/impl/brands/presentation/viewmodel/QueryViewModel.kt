package com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class QueryViewModel @Inject constructor() : DecomposeViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    fun onQueryChanged(value: String) {
        _query.value = value
    }

    fun clearQuery() {
        _query.value = ""
    }
}
