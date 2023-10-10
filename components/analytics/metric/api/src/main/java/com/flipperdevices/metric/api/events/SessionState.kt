package com.flipperdevices.metric.api.events

import android.app.Activity
import android.content.res.Configuration

sealed class SessionState {
    data class StartSession(val activity: Activity) : SessionState()

    data object StopSession : SessionState()

    data class ConfigurationChanged(val configuration: Configuration) : SessionState()
}
