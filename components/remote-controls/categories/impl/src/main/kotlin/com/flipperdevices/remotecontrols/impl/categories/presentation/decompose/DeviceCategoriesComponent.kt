package com.flipperdevices.remotecontrols.impl.categories.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

interface DeviceCategoriesComponent {
    val model: StateFlow<Model>

    fun onCategoryClick(category: DeviceCategory)

    fun onBackClick()

    fun tryLoad()

    sealed interface Model {
        data object Loading : Model
        class Loaded(val deviceTypes: ImmutableList<DeviceCategory>) : Model
        data object Error : Model
    }

    fun interface Factory {
        fun invoke(
            componentContext: ComponentContext,
            onBackClick: DecomposeOnBackParameter,
            onCategoryClick: (categoryId: Long, categoryName: String) -> Unit
        ): DeviceCategoriesComponent
    }
}
