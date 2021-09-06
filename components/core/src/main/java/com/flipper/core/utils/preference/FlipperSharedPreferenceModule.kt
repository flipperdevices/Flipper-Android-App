package com.flipper.core.utils.preference

import android.content.Context
import android.content.SharedPreferences
import com.flipper.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@ContributesTo(AppGraph::class)
class FlipperSharedPreferenceModule {
    @Provides
    @Singleton
    fun provideFlipperSharedPreference(context: Context): FlipperSharedPreferences {
        return FlipperSharedPreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreference(flipperSharedPreferences: FlipperSharedPreferences): SharedPreferences {
        return flipperSharedPreferences
    }
}