package com.flipperdevices.bridge.service.impl.di

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface FlipperServiceComponent {
    val dataStoreSettings: Provider<DataStore<Settings>>
    val applicationParams: ApplicationParams
}
