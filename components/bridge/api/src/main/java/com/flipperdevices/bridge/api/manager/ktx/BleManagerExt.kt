package com.flipperdevices.bridge.api.manager.ktx

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import com.flipperdevices.bridge.api.manager.ktx.providers.BondStateProvider
import com.flipperdevices.bridge.api.manager.ktx.providers.ConnectionStateProvider
import com.flipperdevices.bridge.api.manager.ktx.state.BondState
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
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
    val observer = getConnectionObserver()
    trySend(getConnectionStateFrom(this@stateAsFlow))
    subscribeOnConnectionState(observer)
    awaitClose { unsubscribeConnectionState(observer) }
}

fun BondStateProvider.bondingStateAsFlow() = callbackFlow {
    val bondObserver = getBondingObserver()
    trySend(getBondingStateFrom(this@bondingStateAsFlow))
    subscribeOnBondingState(bondObserver)
    awaitClose { unsubscribeBondingState(bondObserver) }
}

private fun ProducerScope<ConnectionState>.getConnectionObserver() = object : ConnectionObserver {
    override fun onDeviceConnecting(device: BluetoothDevice) {
        trySend(ConnectionState.Connecting)
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        trySend(ConnectionState.Initializing)
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        trySend(ConnectionState.Disconnected(parseDisconnectedReason(reason)))
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        trySend(ConnectionState.Ready)
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        trySend(ConnectionState.Disconnecting)
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        trySend(ConnectionState.Disconnected(parseDisconnectedReason(reason)))
    }
}

private fun getConnectionStateFrom(
    connectionStateProvider: ConnectionStateProvider
): ConnectionState {
    return when (connectionStateProvider.getConnectionState()) {
        BluetoothProfile.STATE_CONNECTING -> ConnectionState.Disconnecting
        BluetoothProfile.STATE_CONNECTED -> if (connectionStateProvider.isReady()) {
            ConnectionState.Ready
        } else ConnectionState.Initializing
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
