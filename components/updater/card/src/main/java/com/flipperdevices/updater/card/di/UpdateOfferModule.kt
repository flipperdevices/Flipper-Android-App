package com.flipperdevices.updater.card.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegate
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegateCurrentRegion
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegateFlagAlways
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegateFlipperManifest
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegateFlipperRegionFile
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
@ContributesTo(AppGraph::class)
class UpdateOfferModule {

    @Provides
    @IntoSet
    fun provideManifest(
        delegateFlipperManifest: UpdateOfferDelegateFlipperManifest
    ): UpdateOfferDelegate = delegateFlipperManifest

    @Provides
    @IntoSet
    fun provideRegionFile(
        delegateFlipperRegionFile: UpdateOfferDelegateFlipperRegionFile
    ): UpdateOfferDelegate = delegateFlipperRegionFile

    @Provides
    @IntoSet
    fun provideCurrentRegion(
        delegateCurrentRegion: UpdateOfferDelegateCurrentRegion
    ): UpdateOfferDelegate = delegateCurrentRegion

    @Provides
    @IntoSet
    fun provideFlagAlwaysUpdate(
        delegateFlagAlways: UpdateOfferDelegateFlagAlways
    ): UpdateOfferDelegate = delegateFlagAlways
}
