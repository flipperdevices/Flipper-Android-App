package com.flipperdevices.widget.screen.fragments

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.widget.screen.compose.WidgetOptionsComposable
import com.flipperdevices.widget.screen.di.WidgetComponent
import javax.inject.Inject

private const val EXTRA_WIDGET_ID_KEY = "widget_id"

class WidgetSelectFragment : ComposeFragment() {

    @Inject
    lateinit var archiveApi: ArchiveApi

    private val widgetAppIdNullable by lazy {
        val savedArg = arguments?.getInt(EXTRA_WIDGET_ID_KEY, -1)
        return@lazy if (savedArg == null || savedArg < 0) {
            null
        } else savedArg
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        val widgetAppId = widgetAppIdNullable

        LaunchedEffect(widgetAppId) {
            if (widgetAppId == null) {
                requireRouter().exit()
            }
        }

        if (widgetAppId == null) {
            return
        }
        WidgetOptionsComposable(archiveApi)
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent

    companion object {
        fun getInstance(widgetId: Int): WidgetSelectFragment {
            return WidgetSelectFragment().withArgs {
                putInt(EXTRA_WIDGET_ID_KEY, widgetId)
            }
        }
    }
}