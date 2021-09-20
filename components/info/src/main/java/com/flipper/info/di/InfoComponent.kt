package com.flipper.info.di

import com.flipper.core.di.AppGraph
import com.flipper.info.main.InfoFragment
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface InfoComponent {
    fun inject(fragment: InfoFragment)
}
