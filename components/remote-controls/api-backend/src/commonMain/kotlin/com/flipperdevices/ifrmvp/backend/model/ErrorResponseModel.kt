package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ErrorResponseModel(
    @SerialName("error_type")
    val errorType: ErrorType
)
