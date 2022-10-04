package com.flipperdevices.widget.screen.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.screen.fragments.WidgetSelectFragment
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface WidgetComponent {
    fun inject(fragment: WidgetSelectFragment)
}