package com.flipperdevices.faphub.errors.api.throwable

import com.flipperdevices.bridge.rpc.api.model.exceptions.NoSdCardException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import java.net.UnknownHostException

enum class FapHubError {
    NO_NETWORK,
    WRONG_REQUEST,
    NOT_FOUND_REQUEST,
    FLIPPER_NOT_CONNECTED,
    NO_SERVER,
    GENERAL,
    FIRMWARE_NOT_SUPPORTED,
    NO_SD_CARD
}

fun Throwable.toFapHubError(): FapHubError {
    return when (this) {
        is UnknownHostException -> FapHubError.NO_NETWORK
        is FirmwareNotSupported -> FapHubError.FIRMWARE_NOT_SUPPORTED
        is JsonConvertException -> FapHubError.WRONG_REQUEST
        is ClientRequestException -> if (response.status == HttpStatusCode.NotFound) {
            FapHubError.NOT_FOUND_REQUEST
        } else {
            FapHubError.WRONG_REQUEST
        }

        is FlipperNotConnected -> FapHubError.FLIPPER_NOT_CONNECTED
        is ServerResponseException -> FapHubError.NO_SERVER
        is NoSdCardException -> FapHubError.NO_SD_CARD
        else -> FapHubError.GENERAL
    }
}
