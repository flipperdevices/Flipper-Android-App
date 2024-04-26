package com.flipperdevices.core.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
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
import javax.inject.Singleton

@Module
@ContributesTo(AppGraph::class)
class FlipperSharedPreferenceModule {
    @Provides
    @Singleton
    fun provideDataStoreSettings(context: Context): DataStore<Settings> {
        return context.dataStoreSettings
    }

    @Provides
    @Singleton
    fun provideDataStorePairSettings(
        context: Context
    ): DataStore<PairSettings> {
        return context.dataStorePairSettings
    }

    @Provides
    @Singleton
    fun provideDataStoreNewPairSetting(
        context: Context
    ): DataStore<NewPairSettings> {
        return context.dataStoreNewPairSettings
    }
}

private val Context.dataStoreNewPairSettings: DataStore<NewPairSettings> by dataStore(
    fileName = FlipperStorageProvider.DATASTORE_FILENAME_PAIR_SETTINGS_V2,
    serializer = NewPairSettingsSerializer
)

private val Context.dataStoreSettings: DataStore<Settings> by dataStore(
    fileName = FlipperStorageProvider.DATASTORE_FILENAME_SETTINGS,
    serializer = SettingsSerializer
)

private val Context.dataStorePairSettings: DataStore<PairSettings> by dataStore(
    fileName = FlipperStorageProvider.DATASTORE_FILENAME_PAIR_SETTINGS,
    serializer = PairSettingsSerializer
)
