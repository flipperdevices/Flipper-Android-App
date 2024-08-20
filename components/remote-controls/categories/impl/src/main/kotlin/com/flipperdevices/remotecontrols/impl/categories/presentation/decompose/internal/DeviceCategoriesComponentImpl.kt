package com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import com.flipperdevices.remotecontrols.impl.categories.presentation.viewmodel.DeviceCategoryListViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, DeviceCategoriesComponent.Factory::class)
class DeviceCategoriesComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBackClick: DecomposeOnBackParameter,
    @Assisted private val onCategoryClick: (categoryId: Long, categoryName: String) -> Unit,
    createDeviceCategoryListViewModel: Provider<DeviceCategoryListViewModel>,
) : DeviceCategoriesComponent,
    ComponentContext by componentContext {
    private val deviceCategoryListFeature = instanceKeeper.getOrCreate {
        createDeviceCategoryListViewModel.get()
    }

    override val model = deviceCategoryListFeature.model

    override fun onCategoryClick(category: DeviceCategory) {
        onCategoryClick(
            category.id,
            category.meta.manifest.singularDisplayName
        )
    }

    override fun onBackClick() = onBackClick.invoke()

    override fun tryLoad() {
        deviceCategoryListFeature.tryLoad()
    }
}
