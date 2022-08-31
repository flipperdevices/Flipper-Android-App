package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.card.helpers.FileExistHelper
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateOfferDelegate::class)
class UpdateOfferDelegateFlipperManifest @Inject constructor(
    private val fileExistHelper: FileExistHelper
) : UpdateOfferDelegate {

    override fun isRequire(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return fileExistHelper
            .isFileExist(Constants.PATH.MANIFEST_FILE, serviceApi.requestApi)
            .map { it.not() }
    }
}
