package com.flipper.pair.impl.navigation.models

import android.os.Build
import androidx.annotation.RequiresApi
import com.flipper.pair.impl.findstandart.StandartFindFragment
import com.flipper.pair.impl.fragments.findcompanion.CompanionFindFragment
import com.flipper.pair.impl.fragments.guide.FragmentGuide
import com.flipper.pair.impl.fragments.permission.PermissionFragment
import com.flipper.pair.impl.fragments.tos.FragmentTOS
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
