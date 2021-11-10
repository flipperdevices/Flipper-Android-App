package com.flipperdevices.bottombar.impl.main.subnavigation

import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router

interface LocalCiceroneHolder {
    fun getCicerone(containerTag: FlipperBottomTab): Cicerone<Router>
}
