package com.flipperdevices.bridge.synchronization.impl.di

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.FlipperFileApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.KeysSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface TaskSynchronizationComponentDependencies {
    val simpleKeyApi: SimpleKeyApi
    val deleteKeyApi: DeleteKeyApi
    val utilsKeyApi: UtilsKeyApi
    val favoriteApi: FavoriteApi
    val flipperFileApi: FlipperFileApi
    val updateKeyApi: UpdateKeyApi
    val settings: DataStore<Settings>
    val storageProvider: FlipperStorageProvider
}

interface TaskSynchronizationComponent {
    val keysSynchronization: KeysSynchronization
    val favoriteSynchronization: FavoriteSynchronization
    val manifestRepository: ManifestRepository

    /**
     * This [ManualFactory] is required to escape from usage of kapt inside this module.
     *
     * [ManualFactory.create] will return manually created [TaskSynchronizationComponent] instance
     */
    object ManualFactory {
        fun create(
            deps: TaskSynchronizationComponentDependencies,
            storageFeatureApi: FStorageFeatureApi
        ): TaskSynchronizationComponent = TaskSynchronizationComponentImpl(
            deps = deps,
            storageFeatureApi = storageFeatureApi
        )
    }
}
