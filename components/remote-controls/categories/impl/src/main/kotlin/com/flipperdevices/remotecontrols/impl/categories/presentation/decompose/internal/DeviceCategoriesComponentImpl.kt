package com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import com.flipperdevices.remotecontrols.api.CategoriesScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import com.flipperdevices.remotecontrols.impl.categories.presentation.viewmodel.DeviceCategoryListViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, DeviceCategoriesComponent.Factory::class)
class DeviceCategoriesComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBackClicked: () -> Unit,
    @Assisted private val onCategoryClicked: (categoryId: Long) -> Unit,
    createDeviceCategoryListViewModel: Provider<DeviceCategoryListViewModel>,
) : DeviceCategoriesComponent,
    ComponentContext by componentContext {
    private val deviceCategoryListFeature = instanceKeeper.getOrCreate {
        createDeviceCategoryListViewModel.get()
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
