package com.flipperdevices.keyscreen.impl.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import javax.inject.Inject

class KeyScreenViewModel(
    keyPath: FlipperKeyPath
) : ViewModel() {

    @Inject
    lateinit var keyApi: KeyApi

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
    }

    fun loadKeyContent() {
    }
}
