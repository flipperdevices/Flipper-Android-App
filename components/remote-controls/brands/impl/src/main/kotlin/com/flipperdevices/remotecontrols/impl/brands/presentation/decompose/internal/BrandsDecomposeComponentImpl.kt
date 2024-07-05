package com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel.BrandsListViewModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel.QueryViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, BrandsDecomposeComponent.Factory::class)
class BrandsDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBackClicked: () -> Unit,
    @Assisted private val onBrandClicked: (brandId: Long) -> Unit,
    @Assisted categoryId: Long,
    createBrandsListViewModel: BrandsListViewModel.Factory,
    createQueryViewModel: Provider<QueryViewModel>
) : BrandsDecomposeComponent, ComponentContext by componentContext {
    private val brandsListFeature = instanceKeeper.getOrCreate {
        createBrandsListViewModel.invoke(categoryId)
    }
    private val queryFeature = instanceKeeper.getOrCreate {
        createQueryViewModel.get()
    }
    override fun model(coroutineScope: CoroutineScope): StateFlow<BrandsDecomposeComponent.Model> = combine(
        flow = queryFeature.query,
        flow2 = brandsListFeature.state,
        transform = { query, pagingState ->
            when (pagingState) {
                is BrandsListViewModel.State.Loading -> {
                    BrandsDecomposeComponent.Model.Loading
                }

                is BrandsListViewModel.State.Loaded -> {
                    BrandsDecomposeComponent.Model.Loaded(
                        brands = pagingState.brands,
                        query = query
                    )
                }

                is BrandsListViewModel.State.Error -> {
                    BrandsDecomposeComponent.Model.Error
                }
            }
        }
    )
        .flowOn(Dispatchers.IO)
        .stateIn(coroutineScope, SharingStarted.Eagerly, BrandsDecomposeComponent.Model.Loading)

    override fun onBackClicked() {
        onBackClicked.invoke()
    }

    override fun onQueryChanged(query: String) {
        queryFeature.onQueryChanged(query)
    }

    override fun clearQuery() {
        queryFeature.clearQuery()
    }

    override fun onBrandClicked(brandModel: BrandModel) {
        onBrandClicked.invoke(brandModel.id)
    }

    override fun tryLoad() {
        brandsListFeature.tryLoad()
    }
}
