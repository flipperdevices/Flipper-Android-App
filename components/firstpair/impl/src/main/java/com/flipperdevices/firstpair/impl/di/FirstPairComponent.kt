package com.flipperdevices.firstpair.impl.di

import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesSubcomponent

@ContributesSubcomponent(
    scope = FirstPairGraph::class,
    parentScope = AppGraph::class
)
interface FirstPairComponent
