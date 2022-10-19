package com.flipperdevices.settings.impl.fragments

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.settings.impl.composable.ComposableSettings
import com.flipperdevices.settings.impl.di.SettingsComponent
import com.flipperdevices.settings.impl.model.NavGraphRoute
import javax.inject.Inject

class SettingsFragment : ComposeFragment(), OnBackPressListener {
    private var navController: NavHostController? = null

    @Inject
    lateinit var aggregatedFeatureEntries: MutableSet<AggregateFeatureEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SettingsComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        navController = rememberNavController()
        navController?.let {
            ComposableSettings(navController = it, aggregatedFeatureEntries)
        }
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent

    override fun onBackPressed(): Boolean {
        navController?.let {
            val currentDestination = it.currentDestination ?: return false
            if (currentDestination.route == NavGraphRoute.Settings.name) return false
            it.popBackStack()
            return true
        }
        return false
    }
}
