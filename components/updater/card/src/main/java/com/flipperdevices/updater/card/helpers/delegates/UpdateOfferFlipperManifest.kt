package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.update.api.FUpdateFeatureApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.card.helpers.FileExistHelper
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateOfferDelegate::class)
class UpdateOfferFlipperManifest @Inject constructor(
    private val fileExistHelper: FileExistHelper
) : UpdateOfferDelegate {

    override fun isRequire(fStorageFeatureApi: FStorageFeatureApi): Flow<Boolean> {
        return fileExistHelper
            .isFileExist(FUpdateFeatureApi.MANIFEST_FILE, fStorageFeatureApi.listingApi())
            .map { it.not() }
    }
}
