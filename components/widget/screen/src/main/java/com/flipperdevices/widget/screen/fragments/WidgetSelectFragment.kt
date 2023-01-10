package com.flipperdevices.widget.screen.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.widget.screen.compose.WidgetOptionsComposable
import com.flipperdevices.widget.screen.di.WidgetComponent
import com.flipperdevices.widget.screen.viewmodel.WidgetSelectViewModel
import tangle.viewmodel.fragment.tangleViewModel
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

const val EXTRA_WIDGET_ID_KEY = "widget_id"

class WidgetSelectFragment : ComposeFragment() {

    @Inject
    lateinit var archiveApi: ArchiveApi

    private val widgetSelectViewModel: WidgetSelectViewModel by tangleViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        WidgetOptionsComposable(archiveApi, widgetSelectViewModel)
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
