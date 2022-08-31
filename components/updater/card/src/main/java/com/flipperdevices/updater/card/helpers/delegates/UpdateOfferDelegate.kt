package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import kotlinx.coroutines.flow.Flow

// After new implementation add class to UpdateOfferModule
interface UpdateOfferDelegate {
    fun isRequire(serviceApi: FlipperServiceApi): Flow<Boolean>
}
