package com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers

sealed class FlipperInformationStatus<T> {
    class NotStarted<T> : FlipperInformationStatus<T>()
    class InProgress<T>(val data: T) : FlipperInformationStatus<T>()
    data class Ready<T>(val data: T) : FlipperInformationStatus<T>()

}


fun <T> FlipperInformationStatus<T>.dataOrNull() = when (this) {
    is FlipperInformationStatus.InProgress -> data
    is FlipperInformationStatus.Ready -> data
    is FlipperInformationStatus.NotStarted -> null
}