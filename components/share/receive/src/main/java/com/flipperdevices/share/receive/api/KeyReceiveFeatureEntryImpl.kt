package com.flipperdevices.share.receive.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.deeplink.model.DeeplinkNavType
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.api.KeyReceiveFeatureEntry
import com.flipperdevices.share.receive.composable.ComposableKeyReceive
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ContributesBinding(AppGraph::class, KeyReceiveFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class KeyReceiveFeatureEntryImpl @Inject constructor(
    private val keyScreenApi: KeyScreenApi
) : KeyReceiveFeatureEntry {
    override fun getKeyReceiveScreen(deeplink: Deeplink): String {
        val strDeeplink = Uri.encode(Json.encodeToString(deeplink))
        return "@${ROUTE.name}?deeplink=$strDeeplink"
    }

    private val keyReceiveArguments = listOf(
        navArgument(DeeplinkConstants.KEY) {
            nullable = false
            type = DeeplinkNavType()
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            "@${ROUTE.name}?deeplink={${DeeplinkConstants.KEY}}",
            arguments = keyReceiveArguments
        ) {
            ComposableKeyReceive(
                keyScreenApi = keyScreenApi,
                onCancel = navController::popBackStack
            )
        }
    }
}
