package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegate
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

interface UpdateOfferProviderApi {
    fun isUpdateRequire(fStorageFeatureApi: FStorageFeatureApi): Flow<Boolean>
}

@ContributesBinding(AppGraph::class, binding<UpdateOfferProviderApi>())
class UpdateOfferProvider @Inject constructor(
    private val delegates: Set<UpdateOfferDelegate>
) : UpdateOfferProviderApi {

    override fun isUpdateRequire(fStorageFeatureApi: FStorageFeatureApi): Flow<Boolean> {
        return combine(
            flows = delegates.map { it.isRequire(fStorageFeatureApi) },
            transform = { delegate -> return@combine delegate.any { it } }
        )
    }
}
