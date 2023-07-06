package com.flipperdevices.bridge.service.impl.di

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.preference.pb.Settings
import javax.inject.Provider

interface FlipperServiceComponent {
    val dataStoreSettings: Provider<DataStore<Settings>>
    val applicationParams: ApplicationParams
}
