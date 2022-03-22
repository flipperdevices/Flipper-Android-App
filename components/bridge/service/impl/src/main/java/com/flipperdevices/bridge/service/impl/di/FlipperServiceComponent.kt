package com.flipperdevices.bridge.service.impl.di

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.impl.FlipperServiceApiImpl
import com.flipperdevices.bridge.service.impl.delegate.FlipperServiceConnectDelegate
import com.flipperdevices.bridge.service.impl.notification.FlipperNotificationHelper
import com.flipperdevices.bridge.service.impl.provider.lifecycle.FlipperServiceConnectionHelperImpl
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface FlipperServiceComponent {
    val dataStoreSettings: Provider<DataStore<Settings>>

    fun inject(delegate: FlipperServiceConnectDelegate)
    fun inject(serviceApi: FlipperServiceApiImpl)
    fun inject(notification: FlipperNotificationHelper)
    fun inject(connectionHelper: FlipperServiceConnectionHelperImpl)
}
