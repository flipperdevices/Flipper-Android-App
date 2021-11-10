package com.flipperdevices.bottombar.impl.main.subnavigation

import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.core.di.AppGraph
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesBinding
import java.util.EnumMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class)
class LocalCiceroneHolderImpl @Inject constructor() : LocalCiceroneHolder {
    private val containers = EnumMap<FlipperBottomTab, Cicerone<Router>>(
        FlipperBottomTab::class.java
    )

    override fun getCicerone(containerTag: FlipperBottomTab): Cicerone<Router> {
        return containers.getOrPut(containerTag) {
            Cicerone.create()
        }
    }
}
