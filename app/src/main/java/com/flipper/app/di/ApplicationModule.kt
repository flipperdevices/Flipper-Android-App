package com.flipper.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideContext() = context
}
