package com.flipperdevices.bridge.connection.di

import com.flipperdevices.bridge.connection.ConnectionTestActivity
import com.flipperdevices.bridge.connection.livetests.RootLiveTest

interface AppComponent {
    val rootLiveTest: RootLiveTest

    fun inject(activity: ConnectionTestActivity)
}
