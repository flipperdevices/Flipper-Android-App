package com.flipper.pair.navigation

import android.content.Context
import android.os.Build
import com.flipper.bridge.utils.DeviceFeatureHelper
import com.flipper.core.di.AppGraph
import com.flipper.pair.find.FindDeviceFragment
import com.flipper.pair.findcompanion.FindDeviceOreoFragment
import com.flipper.pair.permission.PermissionFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class PairNavigationScreensImpl @Inject constructor(private val context: Context) :
    PairNavigationScreens {
    override fun permissionScreen() = FragmentScreen { PermissionFragment() }
    override fun findDeviceScreen(): Screen {
        return FragmentScreen {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                DeviceFeatureHelper.isCompanionFeatureAvailable(context)
            ) {
                FindDeviceOreoFragment()
            } else {
                FindDeviceFragment()
            }
        }
    }
}
