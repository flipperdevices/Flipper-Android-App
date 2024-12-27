package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import kotlinx.coroutines.flow.Flow

interface UpdateOfferDelegate {
    fun isRequire(fStorageFeatureApi: FStorageFeatureApi): Flow<Boolean>
}
