package com.flipperdevices.remotecontrols.impl.categories.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.remotecontrols.impl.categories.presentation.data.DeviceCategoriesRepository
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceCategoryListViewModel @Inject constructor(
    private val deviceCategoriesRepository: DeviceCategoriesRepository
) : DecomposeViewModel() {
    val model =
        MutableStateFlow<DeviceCategoriesComponent.Model>(DeviceCategoriesComponent.Model.Loading)

    fun tryLoad() = viewModelScope.launch {
        model.value = DeviceCategoriesComponent.Model.Loading
        deviceCategoriesRepository.fetchCategories()
            .onFailure { model.value = DeviceCategoriesComponent.Model.Error }
            .onFailure(Throwable::printStackTrace)
            .onSuccess { categories ->
                model.value = DeviceCategoriesComponent.Model.Loaded(categories)
            }
    }

    init {
        tryLoad()
    }
}
