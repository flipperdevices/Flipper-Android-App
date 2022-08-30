package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.keyscreen.api.KeyScreenApi
import javax.inject.Inject

class KeyItemViewModel : ViewModel() {
    @Inject
    lateinit var keyScreenApi: KeyScreenApi

    @Inject
    lateinit var ciceroneGlobal: CiceroneGlobal

    init {
        ComponentHolder.component<ArchiveComponent>().inject(this)
    }

    fun open(path: FlipperKeyPath) {
        ciceroneGlobal.getRouter().navigateTo(keyScreenApi.getKeyScreenScreen(path))
    }
}
