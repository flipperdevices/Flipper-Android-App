package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.dataOrNull
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StorageExistHelper @Inject constructor(
    private val fFeatureProvider: FFeatureProvider
) {
    fun isExternalStorageExist(): Flow<Boolean> {
        return fFeatureProvider.get<FStorageInfoFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FStorageInfoFeatureApi> }
            .flatMapLatest { status ->
                status?.featureApi
                    ?.getStorageInformationFlow()
                    ?: flowOf(null)
            }
            .map { fStorageInfo -> fStorageInfo?.externalStorageStatus }
            .map { status ->
                info { "Storage is ${status?.dataOrNull()}" }
                status is FlipperInformationStatus.Ready<*>
            }
    }

    suspend fun invalidate(
        scope: CoroutineScope,
        force: Boolean
    ) {
        fFeatureProvider.getSync<FStorageInfoFeatureApi>()?.invalidate(
            scope = scope,
            force = force
        )
    }
}
