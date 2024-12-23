package com.flipperdevices.nfc.mfkey32.screen.api

import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileStorageMD5Api
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import com.flipperdevices.nfc.mfkey32.screen.viewmodel.PATH_NONCE_LOG
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
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

    override suspend fun checkBruteforceFileExist(md5StorageApi: FFileStorageMD5Api) {
        val isMd5Exists = md5StorageApi.md5(PATH_NONCE_LOG).isSuccess
        _isBruteforceFileExist = isMd5Exists
        hasNotificationFlow.emit(isMd5Exists)
    }
}
