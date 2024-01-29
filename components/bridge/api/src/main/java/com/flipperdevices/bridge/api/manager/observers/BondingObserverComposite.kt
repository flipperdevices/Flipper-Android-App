package com.flipperdevices.bridge.api.manager.observers

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.observer.BondingObserver

class BondingObserverComposite : BondingObserver {
    private val observers = mutableListOf<BondingObserver>()

    fun addObserver(observer: BondingObserver) {
        if (observers.contains(observer)) {
            return
        }
        observers.add(observer)
    }

    fun removeObserver(observer: BondingObserver) {
        observers.remove(observer)
    }

    override fun onBondingRequired(device: BluetoothDevice) {
        observers.forEach { it.onBondingRequired(device) }
    }

    override fun onBonded(device: BluetoothDevice) {
        observers.forEach { it.onBonded(device) }
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        observers.forEach { it.onBondingFailed(device) }
    }
}
