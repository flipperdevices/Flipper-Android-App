package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.content.Intent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.impl.utils.Constants
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private val PATHS = listOf("o", "mfkey32")

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkMfKey @Inject constructor() : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkFap"

    override fun getPriority(
        context: Context,
        intent: Intent
    ): DeepLinkParserDelegatePriority? {
        val pathSegment = intent.data?.pathSegments

        return when {
            intent.data == null -> null
            !Constants.SUPPORTED_HOSTS.contains(intent.data?.host) -> null
            pathSegment == null -> null
            pathSegment == PATHS -> DeepLinkParserDelegatePriority.HIGH
            else -> null
        }
    }

    override suspend fun fromIntent(context: Context, intent: Intent) = Deeplink.BottomBar.ToolsTab.OpenMfKey
}
