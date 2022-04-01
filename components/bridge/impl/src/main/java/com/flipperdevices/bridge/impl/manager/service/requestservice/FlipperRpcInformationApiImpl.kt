package com.flipperdevices.bridge.impl.manager.service.requestservice

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperRpcInformationApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.infoRequest
import com.flipperdevices.protobuf.system.deviceInfoRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

private const val FLIPPER_PATH_INTERNAL_STORAGE = "/int/"
private const val FLIPPER_PATH_EXTERNAL_STORAGE = "/ext/"

class FlipperRpcInformationApiImpl(
    private val scope: CoroutineScope
) : FlipperRpcInformationApi, LogTagProvider {
    override val TAG = "FlipperRpcInformationApi"
    private val rpcInformationFlow = MutableStateFlow(
        FlipperRpcInformation()
    )

    override fun getRpcInformationFlow() = rpcInformationFlow

    fun initialize(requestApi: FlipperRequestApi) {
        receiveStorageInfo(requestApi, FLIPPER_PATH_EXTERNAL_STORAGE) { totalSpace, freeSpace ->
            rpcInformationFlow.update {
                it.copy(
                    externalStorageTotal = totalSpace,
                    externalStorageFree = freeSpace
                )
            }
        }
        receiveStorageInfo(requestApi, FLIPPER_PATH_INTERNAL_STORAGE) { totalSpace, freeSpace ->
            rpcInformationFlow.update {
                it.copy(
                    internalStorageTotal = totalSpace,
                    internalStorageFree = freeSpace
                )
            }
        }
        requestApi.request(
            main {
                systemDeviceInfoRequest = deviceInfoRequest { }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).onEach { response ->
            if (!response.hasSystemDeviceInfoResponse()) {
                return@onEach
            }
            onApplyInfo(
                response.systemDeviceInfoResponse.key,
                response.systemDeviceInfoResponse.value
            )
        }.launchIn(scope)
    }

    private fun receiveStorageInfo(
        requestApi: FlipperRequestApi,
        storagePath: String,
        spaceInfoReceiver: (Long, Long) -> Unit
    ) {
        requestApi.request(
            main {
                storageInfoRequest = infoRequest {
                    path = storagePath
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).onEach { response ->
            if (response.hasStorageInfoResponse()) {
                return@onEach
            }
            spaceInfoReceiver(
                response.storageInfoResponse.totalSpace,
                response.storageInfoResponse.freeSpace
            )
        }.launchIn(scope)
    }

    private fun onApplyInfo(key: String, value: String) {
        verbose { "Receive: $key=$value" }
        rpcInformationFlow.update {
            it.copy(otherFields = it.otherFields.plus(key to value))
        }
    }
}
