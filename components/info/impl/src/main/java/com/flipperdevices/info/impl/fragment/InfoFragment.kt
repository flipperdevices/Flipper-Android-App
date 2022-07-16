package com.flipperdevices.info.impl.fragment

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.compose.navigation.NavGraphRoute
import com.flipperdevices.info.impl.compose.screens.ComposableDeviceInfoScreen
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.updater.api.UpdaterCardApi
import javax.inject.Inject

class InfoFragment : ComposeFragment(), OnBackPressListener {
    @Inject
    lateinit var updaterCardApi: UpdaterCardApi

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<InfoComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        navController = rememberNavController()
        navController?.let {
            ComposableDeviceInfoScreen(it, updaterCardApi)
        }
    }

    override fun onBackPressed(): Boolean {
        navController?.let {
            val currentDestination = it.currentDestination ?: return false
            if (currentDestination.route == NavGraphRoute.Info.name) return false
            it.popBackStack()
            return true
        }
        return false
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent
}
