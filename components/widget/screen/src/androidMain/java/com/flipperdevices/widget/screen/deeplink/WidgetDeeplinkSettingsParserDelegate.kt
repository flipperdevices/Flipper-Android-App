package com.flipperdevices.widget.screen.deeplink

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private const val EXTRA_ID_WIDGET_INVALID = -1

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class WidgetDeeplinkSettingsParserDelegate @Inject constructor() :
    DeepLinkParserDelegate,
    LogTagProvider {
    override val TAG = "WidgetDeeplinkSettingsParserDelegate"

    override fun getPriority(context: Context, intent: Intent): DeepLinkParserDelegatePriority? {
        return if (isWidgetOptionsIntent(intent)) {
            DeepLinkParserDelegatePriority.HIGH
        } else {
            null
        }
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        if (!isWidgetOptionsIntent(intent)) return null
        val widgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            EXTRA_ID_WIDGET_INVALID
        )
        if (widgetId < 0) {
            return null
        }
        return Deeplink.RootLevel.WidgetOptions(widgetId)
    }

    private fun isWidgetOptionsIntent(intent: Intent): Boolean {
        return intent.action == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE &&
            intent.extras?.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID) == true
    }
}
