package com.flipperdevices.firstpair.impl.fragments

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.firstpair.api.FirstPairFeatureEntry
import com.flipperdevices.firstpair.impl.composable.FirstPairNavigation
import com.flipperdevices.firstpair.impl.di.FirstPairComponent
import kotlinx.collections.immutable.toImmutableSet
import javax.inject.Inject

class FirstPairFragment : ComposeFragment() {

    @Inject
    lateinit var featureEntry: FirstPairFeatureEntry

    @Inject
    lateinit var aggregatesEntries: MutableSet<AggregateFeatureEntry>

    @Inject
    lateinit var composeEntries: MutableSet<ComposableFeatureEntry>

    init {
        ComponentHolder.component<FirstPairComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        val navController = rememberNavController()
        FirstPairNavigation(
            navController = navController,
            featureEntry = featureEntry,
            composeEntries = composeEntries.toImmutableSet(),
            aggregatesEntries = aggregatesEntries.toImmutableSet()
        )
    }
}
