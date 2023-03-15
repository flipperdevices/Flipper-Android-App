package com.flipperdevices.share.receive.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
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

private const val DEEPLINK_KEY = DeeplinkConstants.KEY
private const val DEEPLINK_SCHEME = DeeplinkConstants.SCHEMA
private const val DEEPLINK_KEY_RECEIVE_URL = "${DEEPLINK_SCHEME}key_receive={$DEEPLINK_KEY}"

@ContributesBinding(AppGraph::class, KeyReceiveFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class KeyReceiveFeatureEntryImpl @Inject constructor(
    private val keyScreenApi: KeyScreenApi,
) : KeyReceiveFeatureEntry {
    override fun getKeyReceiveScreen(deeplink: Deeplink): String {
        val strDeeplink = Uri.encode(Json.encodeToString(deeplink))
        return "@${ROUTE.name}?$DEEPLINK_KEY=$strDeeplink"
    }

    override fun getKeyReceiveScreenDeeplinkUrl(deeplink: Deeplink): String {
        val deeplinkStr = Uri.encode(Json.encodeToString(deeplink))
        return "${DEEPLINK_SCHEME}key_receive=$deeplinkStr"
    }

    private val keyReceiveArguments = listOf(
        navArgument(DEEPLINK_KEY) {
            nullable = false
            type = DeeplinkNavType()
        }
    )

    private val deeplinkArguments = listOf(
        navDeepLink { uriPattern = DEEPLINK_KEY_RECEIVE_URL }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            "@${ROUTE.name}?$DEEPLINK_KEY={$DEEPLINK_KEY}",
            arguments = keyReceiveArguments,
            deepLinks = deeplinkArguments
        ) {
            ComposableKeyReceive(
                keyScreenApi = keyScreenApi,
                onCancel = navController::popBackStack
            )
        }
    }
}
