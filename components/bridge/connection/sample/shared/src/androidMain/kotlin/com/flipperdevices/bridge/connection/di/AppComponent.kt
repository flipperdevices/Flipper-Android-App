package com.flipperdevices.bridge.connection.di

import com.flipperdevices.bridge.connection.ConnectionTestActivity

interface AppComponent {
    fun inject(activity: ConnectionTestActivity)
}
