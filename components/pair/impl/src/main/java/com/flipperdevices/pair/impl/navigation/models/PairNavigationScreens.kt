package com.flipperdevices.pair.impl.navigation.models

import android.os.Build
import androidx.annotation.RequiresApi
import com.flipperdevices.pair.impl.findstandart.StandartFindFragment
import com.flipperdevices.pair.impl.fragments.findcompanion.CompanionFindFragment
import com.flipperdevices.pair.impl.fragments.guide.FragmentGuide
import com.flipperdevices.pair.impl.fragments.permission.PermissionFragment
import com.flipperdevices.pair.impl.fragments.tos.FragmentTOS
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen

/**
 * List of module screens for internal usage
 */
object PairNavigationScreens {
    fun tosScreen(): Screen = FragmentScreen { FragmentTOS() }
    fun guideScreen(): Screen = FragmentScreen { FragmentGuide() }
    fun permissionScreen(): Screen = FragmentScreen { PermissionFragment() }
    fun standardPairScreen(): Screen = FragmentScreen { StandartFindFragment() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun companionPairScreen(): Screen = FragmentScreen { CompanionFindFragment() }
}
