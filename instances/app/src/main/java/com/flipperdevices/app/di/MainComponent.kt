package com.flipperdevices.app.di

import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.permission.impl.PermissionRequestHandlerImpl
import com.flipperdevices.notification.api.FlipperAppNotificationApi
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface MainComponent {
    val shake2report: Provider<Shake2ReportApi>
    val synchronizationApi: Provider<SynchronizationApi>
    val notificationApi: Provider<FlipperAppNotificationApi>
    val permissionRequestHandlerImpl: Provider<PermissionRequestHandlerImpl>
}
