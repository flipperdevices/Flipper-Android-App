package com.flipperdevices.bridge.connection.feature.protocolversion.impl.api

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.model.FlipperSupportedState
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.preference.pb.Settings
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Provider
import kotlin.time.Duration

private val API_SUPPORTED_VERSION = SemVer(majorVersion = 0, minorVersion = 3)
private val API_MAX_SUPPORTED_VERSION = SemVer(majorVersion = 1, minorVersion = 0)

class FVersionFeatureApiImpl @AssistedInject constructor(
    settingsStoreProvider: Provider<DataStore<Settings>>,
    @Assisted private val scope: CoroutineScope,
    @Assisted metaInfoApi: FTransportMetaInfoApi
) : FVersionFeatureApi, LogTagProvider {
    override val TAG = "FlipperVersionApi"
    private val settingsStore by settingsStoreProvider

    private val semVerStateFlow = MutableStateFlow<SemVer?>(null)
    private val supportedStateFlow = MutableStateFlow<FlipperSupportedState?>(null)

    init {
        metaInfoApi.get(TransportMetaInfoKey.API_VERSION).onFailure {
            semVerStateFlow.value = SemVer(0, 0)
        }.onSuccess { flow ->
            combine(settingsStore.data, flow) { settings, data ->
                onReceiveVersionRawData(settings.ignore_unsupported_version, data)
            }.launchIn(scope)
        }
    }

    override fun getVersionInformationFlow(): StateFlow<SemVer?> = semVerStateFlow

    override fun getSupportedStateFlow() = supportedStateFlow.asStateFlow()

    private suspend fun onReceiveVersionRawData(
        ignoreUnsupported: Boolean,
        data: ByteArray?
    ) {
        if (data == null) {
            semVerStateFlow.emit(null)
            return
        }
        try {
            val apiVersion = String(data)
            onSupportedVersionReceived(ignoreUnsupported, apiVersion)
        } catch (e: Exception) {
            error(e) { "Failed parse api version $data" }

            setDeviceSupportedStatus(
                ignoreUnsupported,
                FlipperSupportedState.DEPRECATED_FLIPPER
            )
        }
    }

    override suspend fun isSupported(version: SemVer, timeout: Duration): Boolean {
        info { "Check for support version $version with timeout $timeout" }
        val currentVersion = try {
            withTimeoutOrNull(timeout) {
                semVerStateFlow
                    .filterNotNull()
                    .first()
            }
        } catch (exception: Throwable) {
            error(exception) { "Failed receive flipper version" }
            return false
        }
        if (currentVersion == null) {
            warn { "Return false because currentVersion is null" }
            return false
        }
        return currentVersion >= version
    }

    private suspend fun onSupportedVersionReceived(
        ignoreUnsupported: Boolean,
        apiVersion: String
    ) {
        info { "Api version is $apiVersion" }
        val filteredApiVersion = apiVersion.replace("[^0-9.]", "")
        info { "Filtered api version is $filteredApiVersion" }
        val parts = apiVersion.trim().split(".")
        val majorPart = parts.firstOrNull()
        val minorPart = if (parts.size >= 2) parts[1] else null
        info { "Founded ${parts.size} parts. Major part is $majorPart, minor is $minorPart" }
        val versionInformation = SemVer(
            majorVersion = majorPart?.toIntOrNull() ?: 0,
            minorVersion = minorPart?.toIntOrNull() ?: 0
        )
        semVerStateFlow.update { versionInformation }
        setDeviceSupportedStatus(ignoreUnsupported, versionInformation.toSupportedState())
    }

    private suspend fun setDeviceSupportedStatus(
        ignoreUnsupported: Boolean,
        state: FlipperSupportedState
    ) {
        if (ignoreUnsupported) {
            supportedStateFlow.emit(FlipperSupportedState.READY)
            return
        }

        supportedStateFlow.emit(state)
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            scope: CoroutineScope,
            metaInfoApi: FTransportMetaInfoApi
        ): FVersionFeatureApiImpl
    }
}

private fun SemVer.toSupportedState() = when {
    this < API_SUPPORTED_VERSION -> FlipperSupportedState.DEPRECATED_FLIPPER
    this >= API_MAX_SUPPORTED_VERSION -> FlipperSupportedState.DEPRECATED_APPLICATION
    else -> FlipperSupportedState.READY
}
