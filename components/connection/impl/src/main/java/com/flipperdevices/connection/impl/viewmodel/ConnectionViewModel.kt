package com.flipperdevices.connection.impl.viewmodel

import com.flipperdevices.connection.impl.di.ConnectionComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel

class ConnectionViewModel : LifecycleViewModel() {
    init {
        ComponentHolder.component<ConnectionComponent>().inject(this)
    }
}
