package com.flipperdevices.settings.impl.fragments

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.settings.impl.composable.ComposableSettings
import com.flipperdevices.settings.impl.model.NavGraphRoute

class SettingsFragment : ComposeFragment(), OnBackPressListener {

    private lateinit var navController: NavHostController

    @Composable
    override fun RenderView() {
        navController = rememberNavController()
        ComposableSettings(navController = navController)
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.background

    override fun onBackPressed(): Boolean {
        val currentDestination = navController.currentDestination ?: return false
        if (currentDestination.route == NavGraphRoute.Settings.name) return false
        navController.popBackStack()
        return true
    }
}
