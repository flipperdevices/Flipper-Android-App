package com.flipperdevices.bridge.service.impl.delegate.connection

import com.flipperdevices.bridge.api.manager.delegates.FlipperConnectionInformationApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.impl.delegate.FlipperSafeConnectWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class FlipperConnectionInformationApiWrapper(
    private val flipperConnectionSource: FlipperConnectionInformationApi,
    private val safeConnectWrapper: FlipperSafeConnectWrapper
) : FlipperConnectionInformationApi {

    override fun isDeviceConnected() = flipperConnectionSource.isDeviceConnected()

    override fun getConnectionStateFlow(): Flow<ConnectionState> {
        return combine(
            flipperConnectionSource.getConnectionStateFlow(),
            safeConnectWrapper.isConnectingFlow()
        ) { state, isConnecting ->
            when (state) {
                is ConnectionState.Disconnected -> if (isConnecting) {
                    ConnectionState.Connecting
                } else {
                    state
                }

                else -> state
            }
        }
    }

    override fun getConnectedDeviceName() = flipperConnectionSource.getConnectedDeviceName()
}
