package com.flipperdevices.updater.screen.api

import android.app.Activity
import android.net.Uri
import android.view.WindowManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.updater.api.UpdaterFeatureEntry
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.screen.composable.ComposableCancelDialog
import com.flipperdevices.updater.screen.composable.ComposableUpdaterScreen
import com.flipperdevices.updater.screen.model.UpdaterScreenState
import com.flipperdevices.updater.screen.model.navigation.UpdaterRequestType
import com.flipperdevices.updater.screen.viewmodel.FlipperColorViewModel
import com.flipperdevices.updater.screen.viewmodel.UpdaterViewModel
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_UPDATE_REQUEST_KEY = "update_request_key"

@ContributesBinding(AppGraph::class, UpdaterFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class UpdaterFeatureEntryImpl @Inject constructor() : UpdaterFeatureEntry {
    override fun getUpdaterScreen(updateRequest: UpdateRequest?): String {
        val route = "@${ROUTE.name}?request="
        if (updateRequest == null) return route
        return route + Uri.encode(Json.encodeToString(updateRequest))
    }

    private val updaterArguments = listOf(
        navArgument(EXTRA_UPDATE_REQUEST_KEY) {
            nullable = true
            type = UpdaterRequestType()
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?request={$EXTRA_UPDATE_REQUEST_KEY}",
            arguments = updaterArguments
        ) {
            val updateRequest = it.arguments?.parcelable<UpdateRequest>(EXTRA_UPDATE_REQUEST_KEY)
            val flipperColorViewModel = tangleViewModel<FlipperColorViewModel>()
            val updaterViewModel = tangleViewModel<UpdaterViewModel>()

            LaunchedEffect(key1 = Unit) {
                updaterViewModel.start(updateRequest)
            }

            val context = LocalContext.current
            DisposableEffect(Unit) {
                val window = (context as? Activity)?.window
                window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                onDispose {
                    window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            val flipperColor by flipperColorViewModel.getFlipperColor().collectAsState()

            val updaterScreenState by updaterViewModel.getState().collectAsState()
            if (updaterScreenState is UpdaterScreenState.Finish) {
                LaunchedEffect(key1 = Unit) {
                    navController.popBackStack()
                }
            }

            val onAbortUpdate = updaterViewModel::cancelUpdate
            var isCancelDialogOpen by remember { mutableStateOf(false) }
            ComposableUpdaterScreen(
                updaterScreenState = updaterScreenState,
                flipperColor = flipperColor,
                onCancel = { isCancelDialogOpen = true },
                onRetry = { updaterViewModel.retry(updateRequest) }
            )
            if (isCancelDialogOpen) {
                when (updaterScreenState) {
                    is UpdaterScreenState.Failed -> onAbortUpdate()
                    else -> {
                        ComposableCancelDialog(
                            onAbort = {
                                isCancelDialogOpen = false
                                updaterViewModel.cancelUpdate()
                            },
                            onContinue = { isCancelDialogOpen = false }
                        )
                    }
                }
            }
        }
    }
}
