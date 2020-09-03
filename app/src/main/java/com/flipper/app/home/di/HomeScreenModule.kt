package com.flipper.app.home.di

import com.flipper.app.home.repository.ProfileRepository
import com.flipper.app.home.repository.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class HomeScreenModule {
    @Binds
    abstract fun repository(impl: ProfileRepositoryImpl): ProfileRepository
}
