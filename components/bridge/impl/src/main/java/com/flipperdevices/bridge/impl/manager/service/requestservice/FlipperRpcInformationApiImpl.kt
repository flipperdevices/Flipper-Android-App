package com.flipperdevices.bridge.impl.manager.service.requestservice

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperRpcInformationApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.ktx.jre.forEachIterable
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.infoRequest
import com.flipperdevices.protobuf.system.deviceInfoRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

private const val FLIPPER_PATH_INTERNAL_STORAGE = "/int/"
private const val FLIPPER_PATH_EXTERNAL_STORAGE = "/ext/"

class FlipperRpcInformationApiImpl(
    private val scope: CoroutineScope
) : FlipperRpcInformationApi, LogTagProvider {
    override val TAG = "FlipperRpcInformationApi"
    private val rpcInformationFlow = MutableStateFlow(
        FlipperRpcInformation()
    )
    private val requestStatusFlow = MutableStateFlow<FlipperRequestRpcInformationStatus>(
        FlipperRequestRpcInformationStatus.NotStarted
    )
    private val requestJobs = mutableListOf<Job>()

    override fun getRequestRpcInformationStatus() = requestStatusFlow
    override fun getRpcInformationFlow() = rpcInformationFlow

    fun initialize(requestApi: FlipperRequestApi) {
        requestStatusFlow.update { FlipperRequestRpcInformationStatus.InProgress() }
        requestJobs += scope.launch {
            receiveStorageInfo(requestApi, FLIPPER_PATH_EXTERNAL_STORAGE) { totalSpace, freeSpace ->
                rpcInformationFlow.update {
                    it.copy(
                        externalStorageTotal = totalSpace,
                        externalStorageFree = freeSpace
                    )
                }
            }
            requestStatusFlow.update {
                if (it is FlipperRequestRpcInformationStatus.InProgress) {
                    it.copy(externalStorageRequestFinished = true)
                } else it
            }
        }
        requestJobs += scope.launch {
            receiveStorageInfo(requestApi, FLIPPER_PATH_INTERNAL_STORAGE) { totalSpace, freeSpace ->
                rpcInformationFlow.update {
                    it.copy(
                        internalStorageTotal = totalSpace,
                        internalStorageFree = freeSpace
                    )
                }
            }
            requestStatusFlow.update {
                if (it is FlipperRequestRpcInformationStatus.InProgress) {
                    it.copy(internalStorageRequestFinished = true)
                } else it
            }
        }
        requestJobs += scope.launch(Dispatchers.Default) {
            requestApi.request(
                main {
                    systemDeviceInfoRequest = deviceInfoRequest { }
                }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
            ).collect { response ->
                if (!response.hasSystemDeviceInfoResponse()) {
                    return@collect
                }
                onApplyInfo(
                    response.systemDeviceInfoResponse.key,
                    response.systemDeviceInfoResponse.value
                )
            }
            requestStatusFlow.update {
                if (it is FlipperRequestRpcInformationStatus.InProgress) {
                    it.copy(rpcDeviceInfoRequestFinished = true)
                } else it
            }
        }
    }

    fun reset() {
        runBlocking {
            requestJobs.forEachIterable {
                it.cancelAndJoin()
            }
        }
        requestStatusFlow.update { FlipperRequestRpcInformationStatus.NotStarted }
        rpcInformationFlow.update { FlipperRpcInformation() }
    }

    private suspend fun receiveStorageInfo(
        requestApi: FlipperRequestApi,
        storagePath: String,
        spaceInfoReceiver: (Long, Long) -> Unit
    ) = withContext(Dispatchers.Default) {
        requestApi.request(
            main {
                storageInfoRequest = infoRequest {
                    path = storagePath
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).collect { response ->
            if (response.hasStorageInfoResponse()) {
                return@collect
            }
            spaceInfoReceiver(
                response.storageInfoResponse.totalSpace,
                response.storageInfoResponse.freeSpace
            )
        }
    }

    private fun onApplyInfo(key: String, value: String) {
        verbose { "Receive: $key=$value" }
        rpcInformationFlow.update {
            it.copy(otherFields = it.otherFields.plus(key to value))
        }
    }
}
