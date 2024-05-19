package com.flipperdevices.nfc.mfkey32.screen.api

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import com.flipperdevices.nfc.mfkey32.screen.viewmodel.PATH_NONCE_LOG
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.md5sumRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, MfKey32Api::class)
class MfKey32ApiImpl @Inject constructor() : MfKey32Api {
    private var _isBruteforceFileExist: Boolean = false
    override val isBruteforceFileExist: Boolean
        get() = _isBruteforceFileExist

    private val hasNotificationFlow = MutableStateFlow(false)
    override fun hasNotification() = hasNotificationFlow

    override suspend fun checkBruteforceFileExist(
        requestApi: FlipperRequestApi
    ) {
        val response = requestApi.request(
            main {
                storageMd5SumRequest = md5sumRequest {
                    path = PATH_NONCE_LOG
                }
            }.wrapToRequest()
        ).first()
        if (response.hasStorageMd5SumResponse()) {
            _isBruteforceFileExist = true
            hasNotificationFlow.emit(true)
        } else {
            _isBruteforceFileExist = false
            hasNotificationFlow.emit(false)
        }
    }
}
