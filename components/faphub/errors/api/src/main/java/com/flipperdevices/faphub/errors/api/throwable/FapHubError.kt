package com.flipperdevices.faphub.errors.api.throwable

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import java.net.UnknownHostException

enum class FapHubError {
    NO_NETWORK,
    WRONG_REQUEST,
    FLIPPER_NOT_CONNECTED,
    NO_SERVER,
    GENERAL,
    FIRMWARE_NOT_SUPPORTED
}

fun Throwable.toFapHubError(): FapHubError {
    return when (this) {
        is UnknownHostException -> FapHubError.NO_NETWORK
        is FirmwareNotSupported -> FapHubError.FIRMWARE_NOT_SUPPORTED
        is JsonConvertException, is ClientRequestException -> FapHubError.WRONG_REQUEST
        is FlipperNotConnected -> FapHubError.FLIPPER_NOT_CONNECTED
        is ServerResponseException -> FapHubError.NO_SERVER
        else -> FapHubError.GENERAL
    }
}
