package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class UpdateOfferProvider @Inject constructor(
    private val delegates: Set<UpdateOfferDelegate>
) {

    fun isUpdateRequire(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return combine(
            delegates.map { it.isRequire(serviceApi) }
        ) { delegate ->
            return@combine delegate.any { it }
        }
    }
}
