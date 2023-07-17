package com.flipperdevices.faphub.catalogtab.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.catalogtab.api.CatalogTabApi
import com.flipperdevices.faphub.catalogtab.impl.composable.ComposableCatalogTabScreen
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, CatalogTabApi::class)
class CatalogTabApiImpl @Inject constructor(
    private val fapInstallationUIApi: FapInstallationUIApi,
    private val errorsRenderer: FapHubComposableErrorsRenderer
) : CatalogTabApi {
    @Composable
    override fun ComposableCatalogTab(
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
                    fapButtonSize = FapButtonSize.COMPACTED
                )
            }
        )
    }
}
