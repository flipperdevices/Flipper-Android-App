package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegate
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface UpdateOfferProviderApi {
    fun isUpdateRequire(serviceApi: FlipperServiceApi): Flow<Boolean>
}

@ContributesBinding(AppGraph::class, UpdateOfferProviderApi::class)
class UpdateOfferProvider @Inject constructor(
    private val delegates: MutableSet<UpdateOfferDelegate>
) : UpdateOfferProviderApi {

    override fun isUpdateRequire(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return combine(
            delegates.map { it.isRequire(serviceApi) }
        ) { delegate ->
            return@combine delegate.any { it }
        }
    }
}
