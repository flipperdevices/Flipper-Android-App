package com.flipperdevices.faphub.catalogtab.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.faphub.catalogtab.api.CatalogTabApi
import com.flipperdevices.faphub.catalogtab.impl.composable.ComposableCatalogTabScreen
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.CategoriesViewModel
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.FapsListViewModel
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<CatalogTabApi>())
class CatalogTabApiImpl @Inject constructor(
    private val fapInstallationUIApi: FapInstallationUIApi,
    private val errorsRenderer: FapHubComposableErrorsRenderer,
    private val categoriesViewModelProvider: Provider<CategoriesViewModel>,
    private val fapsListViewModelProvider: Provider<FapsListViewModel>
) : CatalogTabApi {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun ComposableCatalogTab(
        componentContext: ComponentContext,
        onOpenFapItem: (FapItemShort) -> Unit,
        onCategoryClick: (FapCategory) -> Unit
    ) {
        ComposableCatalogTabScreen(
            onOpenFapItem = onOpenFapItem,
            onCategoryClick = onCategoryClick,
            errorsRenderer = errorsRenderer,
            installationButton = { fapItem, modifier ->
                fapInstallationUIApi.ComposableButton(
                    config = fapItem?.toFapButtonConfig(),
                    modifier = modifier,
                    fapButtonSize = FapButtonSize.COMPACTED,
                    componentContext = componentContext
                )
            },
            categoriesViewModel = componentContext.viewModelWithFactory(key = null) {
                categoriesViewModelProvider.invoke()
            },
            fapsListViewModel = componentContext.viewModelWithFactory(key = null) {
                fapsListViewModelProvider.invoke()
            }
        )
    }
}
