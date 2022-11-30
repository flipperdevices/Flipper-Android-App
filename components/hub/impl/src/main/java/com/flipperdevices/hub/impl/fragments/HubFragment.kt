package com.flipperdevices.hub.impl.fragments

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.hub.impl.api.HubFeatureEntry
import com.flipperdevices.hub.impl.composable.HubNavigation
import com.flipperdevices.hub.impl.di.HubComponent
import javax.inject.Inject

class HubFragment : ComposeFragment(), OnBackPressListener {
    @Inject
    lateinit var featureEntries: MutableSet<AggregateFeatureEntry>

    @Inject
    lateinit var composableEntries: MutableSet<ComposableFeatureEntry>

    @Inject
    lateinit var hubFeatureEntry: HubFeatureEntry


    init {
        ComponentHolder.component<HubComponent>().inject(this)
    }

    private var navController: NavHostController? = null

    @Composable
    override fun RenderView() {
        navController = rememberNavController()
        navController?.let {
            HubNavigation(
                it, featureEntries,
                composableEntries,
                hubFeatureEntry
            )
        }
    }

    override fun getStatusBarColor(): Int = R.color.accent

    override fun onBackPressed(): Boolean {
        navController?.let {
            val currentDestination = it.currentDestination ?: return false
            if (currentDestination.route == hubFeatureEntry.start()) return false
            it.popBackStack()
            return true
        }
        return false
    }
}
