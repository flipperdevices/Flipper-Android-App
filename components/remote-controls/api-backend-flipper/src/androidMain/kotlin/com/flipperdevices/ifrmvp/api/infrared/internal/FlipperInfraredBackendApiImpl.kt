package com.flipperdevices.ifrmvp.api.infrared.internal

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.flashSdStats
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.errors.api.throwable.FirmwareNotSupported
import com.flipperdevices.faphub.errors.api.throwable.FlipperNotConnected
import com.flipperdevices.faphub.installation.manifest.error.NoSdCardException
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperInfraredBackendApi::class)
class FlipperInfraredBackendApiImpl @Inject constructor(
    private val api: InfraredBackendApi,
    private val flipperTargetProviderApi: FlipperTargetProviderApi,
    private val fFeatureProvider: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : FlipperInfraredBackendApi {
    private suspend fun isSdCardPresent(): Boolean {
        return fFeatureProvider.get<FStorageInfoFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FStorageInfoFeatureApi> }
            .map { status -> status?.featureApi }
            .flatMapLatest { feature -> feature?.getStorageInformationFlow() ?: flowOf(null) }
            .first()
            ?.flashSdStats != null
    }

    private suspend fun isDeviceConnected(): Boolean {
        return fDeviceOrchestrator.getState().first() is FDeviceConnectStatus.Connected
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
