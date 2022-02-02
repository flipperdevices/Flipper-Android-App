package com.flipperdevices.firstpair.impl.storage

interface FirstPairStorage {
    fun isTosPassed(): Boolean
    fun isDeviceSelected(): Boolean
    fun markTosPassed()
    fun markDeviceSelected(deviceId: String?)
}
