package com.flipperdevices.info.impl.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flipperdevices.info.impl.compose.bar.ComposableDeviceBar
import com.flipperdevices.info.impl.compose.elements.ComposableConnectedDeviceActionCard
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate
import com.flipperdevices.info.impl.compose.elements.ComposablePairDeviceActionCard
import com.flipperdevices.info.impl.compose.info.ComposableInfoCard
import com.flipperdevices.info.impl.compose.navigation.NavGraphRoute
import com.flipperdevices.updater.api.UpdaterCardApi

@Composable
fun ComposableDeviceInfoScreen(
    navController: NavHostController,
    updaterCardApi: UpdaterCardApi
) {
    NavHost(navController = navController, startDestination = NavGraphRoute.Info.name) {
        composable(route = NavGraphRoute.Info.name) {
            ComposableDeviceInfoScreenInternal(navController, updaterCardApi)
        }
        composable(route = NavGraphRoute.FullInfo.name) {
            ComposableFullDeviceInfoScreen(navController)
        }
    }
}

@Composable
fun ComposableDeviceInfoScreenInternal(
    navController: NavHostController,
    updaterCardApi: UpdaterCardApi
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ComposableDeviceBar()
        updaterCardApi.ComposableUpdaterCard(
            modifier = Modifier.padding(top = 14.dp)
        )
        ComposableFirmwareUpdate(modifier = Modifier.padding(top = 14.dp))
        ComposableInfoCard(modifier = Modifier.padding(top = 14.dp), navController = navController)
        ComposableConnectedDeviceActionCard(modifier = Modifier.padding(top = 14.dp))
        ComposablePairDeviceActionCard(modifier = Modifier.padding(top = 14.dp, bottom = 14.dp))
    }
}
