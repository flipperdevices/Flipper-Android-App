package com.flipperdevices.screenstreaming.noop

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, ScreenStreamingFeatureEntry::class)
class ScreenStreamingFeatureEntryNoop @Inject constructor() : ScreenStreamingFeatureEntry {

    override fun NavGraphBuilder.navigation(navController: NavHostController) = Unit
}
