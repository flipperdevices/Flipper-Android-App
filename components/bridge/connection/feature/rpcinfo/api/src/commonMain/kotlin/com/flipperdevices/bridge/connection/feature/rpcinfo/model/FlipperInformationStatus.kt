package com.flipperdevices.bridge.connection.feature.rpcinfo.model

sealed class FlipperInformationStatus<T> {
    class NotStarted<T> : FlipperInformationStatus<T>()
    data class InProgress<T>(val data: T) : FlipperInformationStatus<T>()
    data class Ready<T>(val data: T) : FlipperInformationStatus<T>()
}
