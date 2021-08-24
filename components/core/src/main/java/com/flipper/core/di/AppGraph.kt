package com.flipper.core.di

import com.squareup.anvil.annotations.MergeComponent
import javax.inject.Singleton

interface AppGraph

@Singleton
@MergeComponent(AppGraph::class)
interface AppComponent