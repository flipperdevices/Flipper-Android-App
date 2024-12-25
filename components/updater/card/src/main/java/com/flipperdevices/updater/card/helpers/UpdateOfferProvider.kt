package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegate
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

interface UpdateOfferProviderApi {
    fun isUpdateRequire(fStorageFeatureApi: FStorageFeatureApi): Flow<Boolean>
}

@ContributesBinding(AppGraph::class, UpdateOfferProviderApi::class)
class UpdateOfferProvider @Inject constructor(
    private val delegates: MutableSet<UpdateOfferDelegate>
) : UpdateOfferProviderApi {

    override fun isUpdateRequire(fStorageFeatureApi: FStorageFeatureApi): Flow<Boolean> {
        return combine(
            flows = delegates.map { it.isRequire(fStorageFeatureApi) },
            transform = { delegate -> return@combine delegate.any { it } }
        )
    }
}
