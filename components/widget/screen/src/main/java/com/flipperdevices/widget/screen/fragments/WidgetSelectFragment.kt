package com.flipperdevices.widget.screen.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.widget.api.WidgetFeatureEntry
import com.flipperdevices.widget.screen.api.EXTRA_WIDGET_ID_KEY
import com.flipperdevices.widget.screen.compose.WidgetNavigation
import com.flipperdevices.widget.screen.di.WidgetComponent
import kotlinx.collections.immutable.toImmutableSet
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

class WidgetSelectFragment : ComposeFragment() {

    @Inject
    lateinit var featureEntries: MutableSet<AggregateFeatureEntry>

    @Inject
    lateinit var composableEntries: MutableSet<ComposableFeatureEntry>

    @Inject
    lateinit var featureEntry: WidgetFeatureEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    private val widgetId: Int?
        get() = this.arguments?.getInt(EXTRA_WIDGET_ID_KEY)

    @Composable
    override fun RenderView() {
        val navController = rememberNavController()
        val widgetIdNotNull = widgetId ?: return
        WidgetNavigation(
            navController = navController,
            featureEntries = featureEntries.toImmutableSet(),
            composeEntries = composableEntries.toImmutableSet(),
            featureEntry = featureEntry,
            widgetId = widgetIdNotNull
        )
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
