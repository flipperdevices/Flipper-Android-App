package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class KeyItemViewModel : ViewModel() {
    @Inject
    lateinit var keyScreenApi: KeyScreenApi

    init {
        ComponentHolder.component<ArchiveComponent>().inject(this)
    }

    fun open(path: FlipperKeyPath, router: Router) {
        router.navigateTo(keyScreenApi.getKeyScreenScreen(path))
    }
}
