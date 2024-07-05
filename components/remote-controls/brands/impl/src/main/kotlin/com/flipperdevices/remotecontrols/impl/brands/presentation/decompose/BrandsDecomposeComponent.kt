package com.flipperdevices.remotecontrols.impl.brands.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.util.ModelExt.charSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BrandsDecomposeComponent {
    fun model(coroutineScope: CoroutineScope): StateFlow<Model>

    fun onQueryChanged(query: String)

    fun clearQuery()

    fun onBackClicked()

    fun onBrandClicked(brandModel: BrandModel)

    fun tryLoad()

    sealed interface Model {
        data object Loading : Model
        data object Error : Model
        class Loaded(
            val brands: List<BrandModel>,
            val query: String
        ) : Model {
            val groupedBrands = brands.groupBy { brandModel ->
                brandModel.charSection()
            }.toList().sortedBy { it.first }

            val headers = groupedBrands.map { group -> group.first }
        }
    }

    interface Factory {
        fun createBrandsComponent(
            componentContext: ComponentContext,
            categoryId: Long,
            onBackClicked: () -> Unit,
            onBrandClicked: (brandId: Long) -> Unit
        ): BrandsDecomposeComponent
    }
}
