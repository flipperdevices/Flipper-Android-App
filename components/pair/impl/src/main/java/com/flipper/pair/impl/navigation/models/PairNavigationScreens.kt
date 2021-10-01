package com.flipper.pair.impl.navigation.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.flipper.pair.impl.findcompanion.CompanionFindFragment
import com.flipper.pair.impl.findstandart.StandartFindFragment
import com.flipper.pair.impl.fragments.tos.FragmentTOS
import com.flipper.pair.impl.permission.PermissionFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen

/**
 * List of module screens for internal usage
 */
object PairNavigationScreens {
    fun tosScreen(): Screen = FragmentScreen { FragmentTOS() }
    fun guideScreen(): Screen = FragmentScreen { Fragment() }
    fun permissionScreen(): Screen = FragmentScreen { PermissionFragment() }
    fun standardPairScreen(): Screen = FragmentScreen { StandartFindFragment() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun companionPairScreen(): Screen = FragmentScreen { CompanionFindFragment() }
}
