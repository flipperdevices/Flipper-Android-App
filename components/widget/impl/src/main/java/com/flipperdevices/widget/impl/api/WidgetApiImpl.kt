package com.flipperdevices.widget.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.api.WidgetApi
import com.flipperdevices.widget.impl.fragments.WidgetSelectFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class WidgetApiImpl @Inject constructor() : WidgetApi {
    override fun getWidgetOptionsScreen(widgetId: Int): Screen {
        return FragmentScreen { WidgetSelectFragment.getInstance(widgetId) }
    }
}