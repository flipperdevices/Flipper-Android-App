package com.flipperdevices.core.navigation.global

import com.flipperdevices.core.di.AppGraph
import com.github.terrakok.cicerone.Cicerone
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class)
class CiceroneGlobalImpl @Inject constructor() : CiceroneGlobal {
    private val cicerone = Cicerone.create()

    override fun getRouter() = cicerone.router

    override fun getNavigationHolder() = cicerone.getNavigatorHolder()
}
