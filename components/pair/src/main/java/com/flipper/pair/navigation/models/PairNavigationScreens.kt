package com.flipper.pair.navigation.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.flipper.pair.findcompanion.CompanionFindFragment
import com.flipper.pair.findstandart.StandartFindFragment
import com.flipper.pair.permission.PermissionFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen

/**
 * List of module screens for internal usage
 */
object PairNavigationScreens {
    fun tosScreen(): Screen = FragmentScreen { Fragment() }
    fun guideScreen(): Screen = FragmentScreen { Fragment() }
    fun permissionScreen(): Screen = FragmentScreen { PermissionFragment() }
    fun standardPairScreen(): Screen = FragmentScreen { StandartFindFragment() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun companionPairScreen(): Screen = FragmentScreen { CompanionFindFragment() }
}
