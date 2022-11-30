package com.flipperdevices.faphub.catalogtab.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.catalogtab.api.CatalogTabApi
import com.flipperdevices.faphub.catalogtab.impl.composable.ComposableCatalogTabScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, CatalogTabApi::class)
class CatalogTabApiImpl @Inject constructor() : CatalogTabApi {
    @Composable
    override fun ComposableCatalogTab() {
        ComposableCatalogTabScreen()
    }
}
