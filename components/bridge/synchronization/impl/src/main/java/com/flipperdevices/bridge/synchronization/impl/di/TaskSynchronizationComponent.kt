package com.flipperdevices.bridge.synchronization.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.FlipperFileApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.KeysSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component

@ContributesTo(AppGraph::class)
interface TaskSynchronizationComponentDependencies {
    val simpleKeyApi: SimpleKeyApi
    val deleteKeyApi: DeleteKeyApi
    val utilsKeyApi: UtilsKeyApi
    val favoriteApi: FavoriteApi
    val flipperFileApi: FlipperFileApi
    val updateKeyApi: UpdateKeyApi
    val context: Context
    val settings: DataStore<Settings>
    val flipperStorageApi: FlipperStorageApi
}

@SingleIn(TaskGraph::class)
@MergeComponent(TaskGraph::class, dependencies = [TaskSynchronizationComponentDependencies::class])
interface TaskSynchronizationComponent {
    val keysSynchronization: KeysSynchronization
    val favoriteSynchronization: FavoriteSynchronization
    val manifestRepository: ManifestRepository

    @Component.Factory
    interface Factory {
        fun create(
            deps: TaskSynchronizationComponentDependencies,
            @BindsInstance requestApi: FlipperRequestApi,
            @BindsInstance flipperVersionApi: FlipperVersionApi
        ): TaskSynchronizationComponent
    }
}
