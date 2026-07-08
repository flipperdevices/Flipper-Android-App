package com.flipperdevices.core.preference

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.internal.NewPairSettingsSerializer
import com.flipperdevices.core.preference.internal.PairSettingsSerializer
import com.flipperdevices.core.preference.internal.SettingsSerializer
import com.flipperdevices.core.preference.pb.NewPairSettings
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.preference.pb.Settings
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppGraph::class)
interface FlipperSharedPreferenceModule {
    @Provides
    @SingleIn(AppGraph::class)
    fun provideDataStoreSettings(
        scope: CoroutineScope,
        storageProvider: FlipperStorageProvider
    ): DataStore<Settings> {
        return DataStoreFactory.create(
            serializer = SettingsSerializer,
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope + Dispatchers.IO,
            produceFile = {
                storageProvider.rootPath
                    .resolve(SettingsFilePaths.DATASTORE_FILENAME_SETTINGS).toFile()
            }
        )
    }

    @Provides
    @SingleIn(AppGraph::class)
    fun provideDataStorePairSettings(
        scope: CoroutineScope,
        storageProvider: FlipperStorageProvider
    ): DataStore<PairSettings> {
        return DataStoreFactory.create(
            serializer = PairSettingsSerializer,
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope + Dispatchers.IO,
            produceFile = {
                storageProvider.rootPath
                    .resolve(SettingsFilePaths.DATASTORE_FILENAME_PAIR_SETTINGS)
                    .toFile()
            }
        )
    }

    @Provides
    @SingleIn(AppGraph::class)
    fun provideDataStoreNewPairSetting(
        scope: CoroutineScope,
        storageProvider: FlipperStorageProvider
    ): DataStore<NewPairSettings> {
        return DataStoreFactory.create(
            serializer = NewPairSettingsSerializer,
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope + Dispatchers.IO,
            produceFile = {
                storageProvider.rootPath
                    .resolve(SettingsFilePaths.DATASTORE_FILENAME_PAIR_SETTINGS_V2)
                    .toFile()
            }
        )
    }
}
