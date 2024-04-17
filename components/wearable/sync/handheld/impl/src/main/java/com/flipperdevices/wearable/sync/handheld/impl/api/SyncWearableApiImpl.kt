package com.flipperdevices.wearable.sync.handheld.impl.api

import android.app.Application
import android.net.Uri
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.sync.common.WearableSyncItem
import com.flipperdevices.wearable.sync.common.wearableSyncItemData
import com.flipperdevices.wearable.sync.handheld.api.SyncWearableApi
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SyncWearableApi::class)
class SyncWearableApiImpl @Inject constructor(
    application: Application,
    private val favoriteApi: FavoriteApi,
    private val simpleKeyApi: SimpleKeyApi
) : SyncWearableApi, LogTagProvider {
    override val TAG = "SyncWearableApi"

    private val dataClient by lazy { Wearable.getDataClient(application) }

    override suspend fun updateWearableIndex() = withContext(FlipperDispatchers.workStealingDispatcher) {
        val flipperKeys = simpleKeyApi.getAllKeys()
        val itemsToSync = flipperKeys.map { flipperKey ->
            WearableSyncItem(
                path = File(flipperKey.path.pathToKey).absolutePath,
                data = wearableSyncItemData {
                    isFavorite = favoriteApi.isFavorite(flipperKey.getKeyPath())
                }
            )
        }
        val itemsToSyncSet = itemsToSync.toSet()

        val toRemoveUri = mutableListOf<Uri>()
        val alreadyExistItems = dataClient.dataItems.await().map {
            val syncItem = WearableSyncItem.fromDataItem(it)
            if (syncItem == null) {
                toRemoveUri.add(it.uri)
                return@map null
            }
            syncItem to it.uri
        }.filterNotNull()

        toRemoveUri.addAll(
            alreadyExistItems.filterNot {
                itemsToSyncSet.contains(it.first)
            }.map { it.second }
        )
        info { "Item to remove: $toRemoveUri" }
        val toAdd = itemsToSync.minus(alreadyExistItems.map { it.first }.toSet())
        info { "Item to add: $toAdd" }

        toRemoveUri.pmap {
            dataClient.deleteDataItems(it).await()
            info { "Complete delete $it" }
        }
        toAdd.pmap {
            dataClient.putDataItem(
                PutDataRequest.create(it.path).apply {
                    data = it.data.toByteArray()
                }
            ).await()
            info { "Complete add $it" }
        }
        return@withContext
    }
}
