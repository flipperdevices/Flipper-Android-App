package com.flipperdevices.remotecontrols.impl.brands.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.util.charSection
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BrandsDecomposeComponent {
    fun model(coroutineScope: CoroutineScope): StateFlow<Model>

    fun onQueryChanged(query: String)

    fun clearQuery()

    fun onBackClick()

    fun onBrandClick(brandModel: BrandModel)

    fun tryLoad()

    sealed interface Model {
        data object Loading : Model
        data object Error : Model
        class Loaded(
            val brands: ImmutableList<BrandModel>,
            val query: String
        ) : Model {
            private val groupedBrands by lazy {
                brands.groupBy { brandModel ->
                    brandModel.charSection()
                }.toList().sortedBy { it.first }
            }

            val sortedBrands by lazy {
                groupedBrands.flatMap { it.second }.toImmutableList()
            }

            val headers by lazy {
                groupedBrands.map { group -> group.first }.toImmutableSet()
            }
        }
    }

    interface Factory {
        fun createBrandsComponent(
            componentContext: ComponentContext,
            categoryId: Long,
            onBackClick: DecomposeOnBackParameter,
            onBrandClick: (brandId: Long, brandName: String) -> Unit
        ): BrandsDecomposeComponent
    }
}
