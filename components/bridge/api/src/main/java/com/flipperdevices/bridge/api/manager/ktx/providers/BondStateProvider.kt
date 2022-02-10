package com.flipperdevices.bridge.api.manager.ktx.providers

import no.nordicsemi.android.ble.observer.BondingObserver

interface BondStateProvider {
    fun getBondState(): Int?
    fun subscribeOnBondingState(observer: BondingObserver)
    fun unsubscribeBondingState(observer: BondingObserver)
}
