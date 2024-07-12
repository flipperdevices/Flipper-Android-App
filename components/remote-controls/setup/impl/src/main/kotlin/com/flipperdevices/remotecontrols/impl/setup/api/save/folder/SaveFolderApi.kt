package com.flipperdevices.remotecontrols.impl.setup.api.save.folder

import com.flipperdevices.bridge.api.manager.FlipperRequestApi

interface SaveFolderApi {
    suspend fun save(
        requestApi: FlipperRequestApi,
        absolutePath: String
    )
}
