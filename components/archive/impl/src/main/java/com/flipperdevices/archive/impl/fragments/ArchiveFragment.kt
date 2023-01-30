package com.flipperdevices.archive.impl.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.archive.api.ArchiveFeatureEntry
import com.flipperdevices.archive.impl.composable.ArchiveNavigation
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import kotlinx.collections.immutable.toImmutableSet
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

class ArchiveFragment : ComposeFragment(), OnBackPressListener {

    @Inject
    lateinit var synchronizationUiApi: SynchronizationUiApi

    @Inject
    lateinit var archiveFeatureEntry: ArchiveFeatureEntry

    @Inject
    lateinit var aggregatesEntries: MutableSet<AggregateFeatureEntry>

    @Inject
    lateinit var composeEntries: MutableSet<ComposableFeatureEntry>

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<ArchiveComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        navController = rememberNavController()
        navController?.let {
            ArchiveNavigation(
                navController = it,
                featureEntry = archiveFeatureEntry,
                aggregatesEntries = aggregatesEntries.toImmutableSet(),
                composeEntries = composeEntries.toImmutableSet()
            )
        }
    }

    override fun onBackPressed(): Boolean {
        navController?.let {
            val currentDestination = it.currentDestination ?: return false
            if (currentDestination.route == archiveFeatureEntry.ROUTE.name) return false
            it.popBackStack()
            return true
        }
        return false
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent
}
