package com.flipper.pair

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.flipper.bridge.utils.DeviceFeatureHelper
import com.flipper.bridge.utils.PermissionHelper
import com.flipper.core.di.ComponentHolder
import com.flipper.core.navigation.delegates.OnBackPressListener
import com.flipper.pair.di.PairComponent
import com.flipper.pair.navigation.PairNavigationScreens
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class PairScreenActivity : FragmentActivity() {
    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screens: PairNavigationScreens

    private val navigator = AppNavigator(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair)
        ComponentHolder.component<PairComponent>().inject(this)

        if (savedInstanceState == null) {
            if (!PermissionHelper.isBluetoothEnabled()) {
                router.newRootScreen(screens.permissionScreen())
                return
            }
            if (DeviceFeatureHelper.isCompanionFeatureAvailable(this) ||
                PermissionHelper.isPermissionGranted(this)
            ) {
                router.newRootScreen(screens.findDeviceScreen())
            } else {
                router.newRootScreen(screens.permissionScreen())
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if ((fragment as? OnBackPressListener)?.onBackPressed() == true) {
            return
        } else {
            router.exit()
        }
    }
}
