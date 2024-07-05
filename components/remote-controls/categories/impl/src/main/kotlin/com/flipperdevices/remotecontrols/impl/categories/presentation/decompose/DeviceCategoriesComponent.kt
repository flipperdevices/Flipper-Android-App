package com.flipperdevices.remotecontrols.impl.categories.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import kotlinx.coroutines.flow.StateFlow

interface DeviceCategoriesComponent {
    val model: StateFlow<Model>

    fun onCategoryClicked(category: DeviceCategory)

    fun onBackClicked()

    fun tryLoad()

    sealed interface Model {
        data object Loading : Model
        class Loaded(val deviceTypes: List<DeviceCategory>) : Model
        data object Error : Model
    }

    fun interface Factory {
        fun invoke(
            componentContext: ComponentContext,
            onBackClicked: () -> Unit,
            onCategoryClicked: (categoryId: Long) -> Unit
        ): DeviceCategoriesComponent
    }
}
