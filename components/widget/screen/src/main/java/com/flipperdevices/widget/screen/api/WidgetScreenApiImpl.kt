package com.flipperdevices.widget.screen.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.api.WidgetScreenApi
import com.flipperdevices.widget.screen.fragments.WidgetSelectFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class WidgetScreenApiImpl @Inject constructor() : WidgetScreenApi {
    override fun getWidgetOptionsScreen(widgetId: Int): Screen {
        return FragmentScreen { WidgetSelectFragment.getInstance(widgetId) }
    }
}
