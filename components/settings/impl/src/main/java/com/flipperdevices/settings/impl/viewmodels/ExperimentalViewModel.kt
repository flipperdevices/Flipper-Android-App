package com.flipperdevices.settings.impl.viewmodels

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.screenstreaming.api.ScreenStreamingApi
import com.flipperdevices.settings.impl.di.SettingsComponent
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class ExperimentalViewModel : LifecycleViewModel() {
    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var fileManager: FileManagerScreenProvider

    @Inject
    lateinit var screenStreamingApi: ScreenStreamingApi

    init {
        ComponentHolder.component<SettingsComponent>().inject(this)
    }

    fun onOpenFileManager(router: Router) {
        router.navigateTo(fileManager.fileManager())
    }

    fun onScreenStreaming(router: Router) {
        router.navigateTo(screenStreamingApi.provideScreen())
    }
}
