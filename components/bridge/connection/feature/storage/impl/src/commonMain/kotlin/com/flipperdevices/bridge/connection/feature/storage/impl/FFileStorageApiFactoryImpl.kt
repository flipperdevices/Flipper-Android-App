package com.flipperdevices.bridge.connection.feature.storage.impl

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.getUnsafe
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.FFileStorageMD5ApiImpl
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.delete.FFileDeleteApiImpl
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.download.FFileDownloadApiImpl
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing.FListingStorageApiImpl
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing.FlipperListingDelegateDeprecated
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing.FlipperListingDelegateNew
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.timestamp.FFileTimestampApiImpl
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.timestamp.FFileTimestampApiNoop
import com.flipperdevices.bridge.connection.feature.storage.impl.fm.upload.FFileUploadApiImpl
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

private val API_SUPPORTED_MD5_LISTING = SemVer(
    majorVersion = 0,
    minorVersion = 20
)
private val API_SUPPORTED_TIMESTAMP = SemVer(majorVersion = 0, minorVersion = 13)

@FDeviceFeatureQualifier(FDeviceFeature.STORAGE)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FFileStorageApiFactoryImpl @Inject constructor() : FDeviceFeatureApi.Factory {
    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val rpcApi = unsafeFeatureDeviceApi.getUnsafe<FRpcFeatureApi>() ?: return null
        val versionApi = unsafeFeatureDeviceApi.getUnsafe<FVersionFeatureApi>()
            ?: return null
        val listingDelegate = if (versionApi.isSupported(API_SUPPORTED_MD5_LISTING)) {
            FlipperListingDelegateNew(rpcApi)
        } else {
            FlipperListingDelegateDeprecated(rpcApi)
        }

        return FStorageFeatureApiImpl(
            md5Api = FFileStorageMD5ApiImpl(rpcApi),
            fListingStorageApi = FListingStorageApiImpl(listingDelegate),
            fileUploadApi = FFileUploadApiImpl(rpcApi, scope = scope),
            fileDownloadApi = FFileDownloadApiImpl(rpcApi, scope = scope),
            deleteApi = FFileDeleteApiImpl(rpcApi),
            timestampApi = if (versionApi.isSupported(API_SUPPORTED_TIMESTAMP)) {
                FFileTimestampApiImpl(rpcApi)
            } else {
                FFileTimestampApiNoop()
            }
        )
    }
}
