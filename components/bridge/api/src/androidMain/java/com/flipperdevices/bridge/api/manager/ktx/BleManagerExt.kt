package com.flipperdevices.bridge.api.manager.ktx

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import com.flipperdevices.bridge.api.manager.ktx.providers.BondStateProvider
import com.flipperdevices.bridge.api.manager.ktx.providers.ConnectionStateProvider
import com.flipperdevices.bridge.api.manager.ktx.state.BondState
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.observers.SuspendConnectionObserver
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import no.nordicsemi.android.ble.observer.BondingObserver
import no.nordicsemi.android.ble.observer.ConnectionObserver

/**
 * Copy from https://github.com/NordicSemiconductor/Android-BLE-Library/blob/8fc0fcddba/ble-ktx/src/main/java/no/nordicsemi/android/ble/ktx/BleManagerExt.kt
 * Original implementation doesn't support multiple observers
 */

fun ConnectionStateProvider.stateAsFlow() = callbackFlow {
    val observer = getConnectionObserver(this@stateAsFlow)
    send(getConnectionStateFrom(this@stateAsFlow))
    subscribeOnConnectionState(observer)
    awaitClose { unsubscribeConnectionState(observer) }
}

fun BondStateProvider.bondingStateAsFlow() = callbackFlow {
    val bondObserver = getBondingObserver()
    send(getBondingStateFrom(this@bondingStateAsFlow))
    subscribeOnBondingState(bondObserver)
    awaitClose { unsubscribeBondingState(bondObserver) }
}

private fun ProducerScope<ConnectionState>.getConnectionObserver(
    connectionStateProvider: ConnectionStateProvider
) = object : SuspendConnectionObserver {
    override suspend fun onDeviceConnecting(device: BluetoothDevice) {
        send(ConnectionState.Connecting)
    }

    override suspend fun onDeviceConnected(device: BluetoothDevice) {
        send(ConnectionState.Initializing)
    }

    override suspend fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        send(ConnectionState.Disconnected(parseDisconnectedReason(reason)))
    }

    override suspend fun onDeviceReady(device: BluetoothDevice) {
        val supportedState = connectionStateProvider.supportState()
        if (supportedState == null) {
            send(ConnectionState.RetrievingInformation)
        } else {
            send(ConnectionState.Ready(supportedState))
        }
    }

    override suspend fun onDeviceDisconnecting(device: BluetoothDevice) {
        send(ConnectionState.Disconnecting)
    }

    override suspend fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        send(ConnectionState.Disconnected(parseDisconnectedReason(reason)))
    }
}

private fun getConnectionStateFrom(
    connectionStateProvider: ConnectionStateProvider
): ConnectionState {
    return when (connectionStateProvider.getConnectionState()) {
        BluetoothProfile.STATE_CONNECTING -> ConnectionState.Connecting
        BluetoothProfile.STATE_CONNECTED -> if (connectionStateProvider.isReady()) {
            val supportedState = connectionStateProvider.supportState()
            if (supportedState == null) {
                ConnectionState.RetrievingInformation
            } else {
                ConnectionState.Ready(supportedState)
            }
        } else {
            ConnectionState.Initializing
        }
        BluetoothProfile.STATE_DISCONNECTING -> ConnectionState.Disconnecting
        else -> ConnectionState.Disconnected(ConnectionState.Disconnected.Reason.UNKNOWN)
    }
}

private fun parseDisconnectedReason(
    reason: Int
): ConnectionState.Disconnected.Reason =
    when (reason) {
        ConnectionObserver.REASON_SUCCESS -> ConnectionState.Disconnected.Reason.SUCCESS
        ConnectionObserver.REASON_TERMINATE_LOCAL_HOST ->
            ConnectionState.Disconnected.Reason.TERMINATE_LOCAL_HOST
        ConnectionObserver.REASON_TERMINATE_PEER_USER ->
            ConnectionState.Disconnected.Reason.TERMINATE_PEER_USER
        ConnectionObserver.REASON_LINK_LOSS -> ConnectionState.Disconnected.Reason.LINK_LOSS
        ConnectionObserver.REASON_NOT_SUPPORTED -> ConnectionState.Disconnected.Reason.NOT_SUPPORTED
        ConnectionObserver.REASON_CANCELLED -> ConnectionState.Disconnected.Reason.CANCELLED
        ConnectionObserver.REASON_TIMEOUT -> ConnectionState.Disconnected.Reason.TIMEOUT
        else -> ConnectionState.Disconnected.Reason.UNKNOWN
    }

@SuppressLint("MissingPermission")
private fun getBondingStateFrom(bondStateProvider: BondStateProvider): BondState {
    return when (bondStateProvider.getBondState()) {
        BluetoothDevice.BOND_BONDED -> BondState.Bonded
        BluetoothDevice.BOND_BONDING -> BondState.Bonding
        else -> BondState.NotBonded
    }
}

private fun ProducerScope<BondState>.getBondingObserver() = object : BondingObserver {
    override fun onBondingRequired(device: BluetoothDevice) {
        trySend(BondState.Bonding)
    }

    override fun onBonded(device: BluetoothDevice) {
        trySend(BondState.Bonded)
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        trySend(BondState.Bonding)
    }
}
