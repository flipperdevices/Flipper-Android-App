package com.flipperdevices.remotecontrols.impl.setup.api.save.folder

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.mkdirRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SaveFolderApi::class)
class SaveFolderApiImpl @Inject constructor() : SaveFolderApi {
    override suspend fun save(
        requestApi: FlipperRequestApi,
        absolutePath: String
    ) {
        requestApi.request(
            command = main {
                hasNext = false
                storageMkdirRequest = mkdirRequest {
                    path = absolutePath
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND),
        ).collect()
    }
}
