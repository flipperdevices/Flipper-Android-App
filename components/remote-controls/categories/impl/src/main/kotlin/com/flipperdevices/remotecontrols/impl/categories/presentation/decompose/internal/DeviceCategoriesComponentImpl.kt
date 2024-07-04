package com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import com.flipperdevices.remotecontrols.impl.categories.presentation.viewmodel.DeviceCategoryListViewModel
import kotlinx.coroutines.flow.asStateFlow

internal class DeviceCategoriesComponentImpl(
    componentContext: ComponentContext,
    createDeviceCategoryListViewModel: () -> DeviceCategoryListViewModel,
    private val onBackClicked: () -> Unit,
    private val onCategoryClicked: (categoryId: Long) -> Unit
) : DeviceCategoriesComponent,
    ComponentContext by componentContext {
    private val deviceCategoryListFeature = instanceKeeper.getOrCreate {
        createDeviceCategoryListViewModel.invoke()
    }

    override val model = deviceCategoryListFeature.model.asStateFlow()

    override fun onCategoryClicked(category: DeviceCategory) {
        onCategoryClicked.invoke(category.id)
    }

    override fun onBackClicked() = onBackClicked.invoke()

    override fun tryLoad() {
        deviceCategoryListFeature.tryLoad()
    }
}
