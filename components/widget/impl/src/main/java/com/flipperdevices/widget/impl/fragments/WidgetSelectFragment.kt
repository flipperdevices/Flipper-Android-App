package com.flipperdevices.widget.impl.fragments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.fragment.ComposeFragment

private const val EXTRA_WIDGET_ID_KEY = "widget_id"

class WidgetSelectFragment : ComposeFragment() {
    private val widgetAppIdNullable by lazy {
        arguments?.getInt(EXTRA_WIDGET_ID_KEY)
    }

    @Composable
    override fun RenderView() {
        val widgetAppId = widgetAppIdNullable

        LaunchedEffect(widgetAppId) {
            if (widgetAppId == null || widgetAppId < 0) {
                requireRouter().exit()
            }
        }
    }

    companion object {
        fun getInstance(widgetId: Int): WidgetSelectFragment {
            return WidgetSelectFragment().withArgs {
                putInt(EXTRA_WIDGET_ID_KEY, widgetId)
            }
        }
    }
}