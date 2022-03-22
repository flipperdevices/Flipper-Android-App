package com.flipperdevices.settings.impl.viewmodels

import androidx.lifecycle.ViewModel
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.debug.api.StressTestApi
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.settings.impl.di.SettingsComponent
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class DebugViewModel : ViewModel() {
    @Inject
    lateinit var stressTestApi: StressTestApi

    @Inject
    lateinit var cicerone: CiceroneGlobal

    @Inject
    lateinit var firstPairApi: FirstPairApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    init {
        ComponentHolder.component<SettingsComponent>().inject(this)
    }

    fun onOpenStressTest(router: Router) {
        router.navigateTo(stressTestApi.getStressTestScreen())
    }

    fun onStartSynchronization() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun onOpenConnectionScreen() {
        cicerone.getRouter().navigateTo(firstPairApi.getFirstPairScreen())
    }
}
