package com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import com.flipperdevices.remotecontrols.impl.categories.presentation.viewmodel.DeviceCategoryListViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.kulikov.metro.assisted.ContributesAssistedFactory
import dev.zacsweers.metro.Provider

@ContributesAssistedFactory(AppGraph::class, DeviceCategoriesComponent.Factory::class)
class DeviceCategoriesComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBackClick: DecomposeOnBackParameter,
    @Assisted private val onCategoryClick: (Long, String) -> Unit,
    createDeviceCategoryListViewModel: Provider<DeviceCategoryListViewModel>,
) : DeviceCategoriesComponent,
    ComponentContext by componentContext {
    private val deviceCategoryListFeature = instanceKeeper.getOrCreate {
        createDeviceCategoryListViewModel.invoke()
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
