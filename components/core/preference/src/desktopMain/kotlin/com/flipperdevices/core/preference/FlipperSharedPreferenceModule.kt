package com.flipperdevices.core.preference

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.internal.NewPairSettingsSerializer
import com.flipperdevices.core.preference.internal.PairSettingsSerializer
import com.flipperdevices.core.preference.internal.SettingsSerializer
import com.flipperdevices.core.preference.pb.NewPairSettings
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus
import java.io.File
import javax.inject.Singleton

@Module
@ContributesTo(AppGraph::class)
class FlipperSharedPreferenceModule {
    @Provides
    @Singleton
    fun provideDataStoreSettings(scope: CoroutineScope): DataStore<Settings> {
        return DataStoreFactory.create(
            serializer = SettingsSerializer,
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope + Dispatchers.IO,
            produceFile = {
                File(
                    FlipperStorageProvider.getAppDir(),
                    SettingsFilePaths.DATASTORE_FILENAME_SETTINGS
                )
            }
        )
    }

    @Provides
    @Singleton
    fun provideDataStorePairSettings(scope: CoroutineScope): DataStore<PairSettings> {
        return DataStoreFactory.create(
            serializer = PairSettingsSerializer,
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope + Dispatchers.IO,
            produceFile = {
                File(
                    FlipperStorageProvider.getAppDir(),
                    SettingsFilePaths.DATASTORE_FILENAME_SETTINGS
                )
            }
        )
    }

    @Provides
    @Singleton
    fun provideDataStoreNewPairSetting(scope: CoroutineScope): DataStore<NewPairSettings> {
        return DataStoreFactory.create(
            serializer = NewPairSettingsSerializer,
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope + Dispatchers.IO,
            produceFile = {
                File(
                    FlipperStorageProvider.getAppDir(),
                    SettingsFilePaths.DATASTORE_FILENAME_SETTINGS
                )
            }
        )
    }
}
