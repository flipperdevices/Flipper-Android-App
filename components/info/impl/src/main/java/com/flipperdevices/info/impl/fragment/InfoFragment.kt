package com.flipperdevices.info.impl.fragment

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.api.InfoFeatureEntry
import com.flipperdevices.info.impl.compose.InfoNavigation
import com.flipperdevices.info.impl.di.InfoComponent
import javax.inject.Inject

class InfoFragment : ComposeFragment(), OnBackPressListener {
    @Inject
    lateinit var featureEntries: MutableSet<AggregateFeatureEntry>

    @Inject
    lateinit var infoFeatureEntry: InfoFeatureEntry

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<InfoComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        navController = rememberNavController()
        navController?.let {
            InfoNavigation(
                navController = it,
                featureEntries = featureEntries,
                infoFeatureEntry = infoFeatureEntry
            )
        }
    }

    override fun onBackPressed(): Boolean {
        navController?.let {
            val currentDestination = it.currentDestination ?: return false
            if (currentDestination.route == infoFeatureEntry.ROUTE.name) return false
            it.popBackStack()
            return true
        }
        return false
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent
}
