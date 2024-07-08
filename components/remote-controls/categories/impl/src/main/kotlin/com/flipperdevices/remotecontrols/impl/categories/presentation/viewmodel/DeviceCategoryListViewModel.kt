package com.flipperdevices.remotecontrols.impl.categories.presentation.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.remotecontrols.impl.categories.presentation.data.DeviceCategoriesRepository
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceCategoryListViewModel @Inject constructor(
    private val deviceCategoriesRepository: DeviceCategoriesRepository
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "DeviceCategoryListViewModel"
    val model = MutableStateFlow<DeviceCategoriesComponent.Model>(
        value = DeviceCategoriesComponent.Model.Loading
    )

    fun tryLoad() = viewModelScope.launch {
        model.value = DeviceCategoriesComponent.Model.Loading
        deviceCategoriesRepository.fetchCategories()
            .onFailure { model.value = DeviceCategoriesComponent.Model.Error }
            .onFailure { throwable -> error(throwable) { "#tryLoad could not fetch categories" } }
            .onSuccess { categories ->
                model.value = DeviceCategoriesComponent.Model.Loaded(categories.toImmutableList())
            }
    }

    init {
        tryLoad()
    }
}
