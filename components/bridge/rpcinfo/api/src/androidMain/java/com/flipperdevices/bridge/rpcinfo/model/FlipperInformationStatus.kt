package com.flipperdevices.bridge.rpcinfo.model

sealed class FlipperInformationStatus<T> {
    class NotStarted<T> : FlipperInformationStatus<T>()
    class InProgress<T>(val data: T) : FlipperInformationStatus<T>()
    data class Ready<T>(val data: T) : FlipperInformationStatus<T>()
}
