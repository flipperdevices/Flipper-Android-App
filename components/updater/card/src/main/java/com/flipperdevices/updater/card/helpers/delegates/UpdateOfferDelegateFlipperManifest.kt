package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.updater.card.di.CardComponent
import com.flipperdevices.updater.card.helpers.FileExistHelper
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UpdateOfferDelegateFlipperManifest @Inject constructor(
    private val fileExistHelper: FileExistHelper
) : UpdateOfferDelegate {


    init {
        ComponentHolder.component<CardComponent>().inject(this)
    }

    override fun isRequire(serviceApi: FlipperServiceApi): Flow<Boolean> {
        return fileExistHelper
            .isFileExist(Constants.PATH.MANIFEST_FILE, serviceApi.requestApi)
            .map { it.not() }
    }
}
