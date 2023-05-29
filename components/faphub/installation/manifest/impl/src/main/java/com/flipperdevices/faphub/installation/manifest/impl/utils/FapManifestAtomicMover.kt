package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.flipperdevices.protobuf.storage.mkdirRequest
import com.flipperdevices.protobuf.storage.renameRequest
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val UNIX_MV_SUPPORTED_VERSION_API = SemVer(majorVersion = 0, minorVersion = 17)

class FapManifestAtomicMover @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider
) {
    suspend fun atomicMove(
        vararg fromToPair: Pair<String, String>
    ) {
        val serviceApi = flipperServiceProvider.getServiceApi()
        createManifestFolder(serviceApi.requestApi)
        val preparedRequest = getPrepareRequests(
            serviceApi.flipperVersionApi,
            fromToPair.map { it.second }
        )
        val moveRequests = getRequests(fromToPair)

        withContext(NonCancellable) {
            @Suppress("SpreadOperator")
            serviceApi.requestApi.requestWithoutAnswer(*preparedRequest.toTypedArray())
            moveRequests.map {
                serviceApi.requestApi.request(it)
            }.map { it.first() }.forEach { response ->
                if (response.commandStatus != Flipper.CommandStatus.OK) {
                    error("Failed move path, failed response: $response")
                }
            }
        }
    }

    private suspend fun createManifestFolder(requestApi: FlipperRequestApi) {
        val response = requestApi.request(
            main {
                storageMkdirRequest = mkdirRequest {
                    path = FAP_MANIFESTS_FOLDER_ON_FLIPPER
                }
            }.wrapToRequest()
        ).first()
        if (response.commandStatus != Flipper.CommandStatus.OK
            && response.commandStatus != Flipper.CommandStatus.ERROR_STORAGE_EXIST
        ) {
            error("Failed create $FAP_MANIFESTS_FOLDER_ON_FLIPPER with command status ${response.commandStatus}")
        }
    }

    private suspend fun getPrepareRequests(
        versionApi: FlipperVersionApi,
        targets: List<String>
    ): List<FlipperRequest> {
        val version = versionApi.getVersionInformationFlow().first()
        if (version != null && version >= UNIX_MV_SUPPORTED_VERSION_API) {
            return emptyList()
        }
        return targets.map { target ->
            main {
                storageDeleteRequest = deleteRequest {
                    path = target
                }
            }.wrapToRequest()
        }
    }

    private fun getRequests(
        fromToPair: Array<out Pair<String, String>>
    ) = fromToPair.map { (from, to) ->
        main {
            storageRenameRequest = renameRequest {
                oldPath = from
                newPath = to
            }
        }.wrapToRequest()
    }
}
