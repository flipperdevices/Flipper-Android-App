package com.flipperdevices.faphub.installedtab.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.installedtab.api.FapNeedUpdatePopUpHelper
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.FapInstalledInternalLoadingState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsFromNetworkProducer
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

@SingleIn(AppGraph::class)
@ContributesBinding(AppGraph::class, binding<FapNeedUpdatePopUpHelper>())
class FapNeedUpdatePopUpHelperImpl @Inject constructor(
    globalScopeProvider: Provider<CoroutineScope>,
    inAppNotificationStorageProvider: Provider<InAppNotificationStorage>,
    installedFapsNetworkProducerProvider: Provider<InstalledFapsFromNetworkProducer>
) : FapNeedUpdatePopUpHelper, LogTagProvider {
    override val TAG = "FapNeedUpdatePopUpHelper"

    private val globalScope by globalScopeProvider
    private val inAppNotificationStorage by inAppNotificationStorageProvider
    private val installedFapsNetworkProducer by installedFapsNetworkProducerProvider

    private val mutex = Mutex()
    private var notificationJob: Job? = null

    override fun notifyIfUpdateAvailable() = launchWithLock(mutex, globalScope, "notify") {
        val oldJob = notificationJob
        notificationJob = globalScope.launch {
            oldJob?.cancelAndJoin()
            installedFapsNetworkProducer.refresh(force = false)
            val loadedFaps = installedFapsNetworkProducer.getLoadedFapsFlow()
                .filterIsInstance<FapInstalledInternalLoadingState.Loaded>()
                .first()
            info { "Loaded faps $loadedFaps" }
            val readyToUpdateCount = loadedFaps
                .faps
                .count { (_, state) -> state is FapInstalledInternalState.ReadyToUpdate }
            info { "Ready to update count $loadedFaps" }
            if (readyToUpdateCount > 0) {
                inAppNotificationStorage.addNotification(InAppNotification.ReadyToUpdateFaps)
            }
        }
    }
}
