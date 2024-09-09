package com.flipperdevices.ifrmvp.api.infrared.internal

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.rpc.api.model.exceptions.NoSdCardException
import com.flipperdevices.bridge.rpcinfo.api.FlipperStorageInformationApi
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.StorageStats
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.errors.api.throwable.FirmwareNotSupported
import com.flipperdevices.faphub.errors.api.throwable.FlipperNotConnected
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import com.flipperdevices.ifrmvp.api.infrared.FlipperInfraredBackendApi
import com.flipperdevices.ifrmvp.api.infrared.InfraredBackendApi
import com.flipperdevices.ifrmvp.backend.model.BrandsResponse
import com.flipperdevices.ifrmvp.backend.model.CategoriesResponse
import com.flipperdevices.ifrmvp.backend.model.IfrFileContentResponse
import com.flipperdevices.ifrmvp.backend.model.InfraredsResponse
import com.flipperdevices.ifrmvp.backend.model.PagesLayoutBackendModel
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperInfraredBackendApi::class)
class FlipperInfraredBackendApiImpl @Inject constructor(
    private val api: InfraredBackendApi,
    private val flipperTargetProviderApi: FlipperTargetProviderApi,
    private val flipperServiceProvider: FlipperServiceProvider,
    private val flipperStorageInformationApi: FlipperStorageInformationApi,
) : FlipperInfraredBackendApi {
    private suspend fun isSdCardPresent(): Boolean {
        val stats = flipperStorageInformationApi.getStorageInformationFlow()
            .map { fStorageInformation -> fStorageInformation.externalStorageStatus }
            .filterIsInstance<FlipperInformationStatus.Ready<StorageStats?>>()
            .map { fStatusInformation -> fStatusInformation.data }
            .filterNotNull()
            .first()
        return stats is StorageStats.Loaded
    }

    private suspend fun isDeviceConnected(): Boolean {
        return flipperServiceProvider.getServiceApi()
            .connectionInformationApi
            .getConnectionStateFlow()
            .first() is ConnectionState.Ready
    }

    @Suppress("ThrowsCount", "RethrowCaughtException")
    private suspend fun <T> wrapRequest(block: suspend () -> T): T {
        return try {
            when (flipperTargetProviderApi.getFlipperTarget().value) {
                FlipperTarget.NotConnected -> throw FlipperNotConnected()
                FlipperTarget.Unsupported -> throw FirmwareNotSupported()
                else -> Unit
            }
            if (!isDeviceConnected()) {
                throw FlipperNotConnected()
            }
            if (!isSdCardPresent()) {
                throw NoSdCardException()
            }
            block.invoke()
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun getCategories(): CategoriesResponse {
        return wrapRequest { api.getCategories() }
    }

    override suspend fun getManufacturers(categoryId: Long): BrandsResponse {
        return wrapRequest { api.getManufacturers(categoryId) }
    }

    override suspend fun getSignal(request: SignalRequestModel): SignalResponseModel {
        return wrapRequest { api.getSignal(request) }
    }

    override suspend fun getIfrFileContent(ifrFileId: Long): IfrFileContentResponse {
        return wrapRequest { api.getIfrFileContent(ifrFileId) }
    }

    override suspend fun getUiFile(ifrFileId: Long): PagesLayoutBackendModel {
        return wrapRequest { api.getUiFile(ifrFileId) }
    }

    override suspend fun getInfrareds(brandId: Long): InfraredsResponse {
        return wrapRequest { api.getInfrareds(brandId) }
    }
}
