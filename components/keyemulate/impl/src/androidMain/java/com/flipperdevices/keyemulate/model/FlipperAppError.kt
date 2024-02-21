package com.flipperdevices.keyemulate.model

sealed class FlipperAppError {
    object NotSupportedApi : FlipperAppError()
    object ForbiddenFrequency : FlipperAppError()
    object NotSupportedApp : FlipperAppError()
    object BadResponse : FlipperAppError()
    data class General(val text: String) : FlipperAppError()
    companion object {

        private const val NOT_SUPPORTED_APP_CODE = 0
        private const val FORBIDDEN_FREQUENCY_CODE = 2

        private const val GENERAL_ERROR_TEXT = "Something went wrong"
        fun fromCode(code: Int, text: String?): FlipperAppError {
            return when (code) {
                NOT_SUPPORTED_APP_CODE -> NotSupportedApp
                FORBIDDEN_FREQUENCY_CODE -> ForbiddenFrequency
                else -> General(text = text ?: GENERAL_ERROR_TEXT)
            }
        }
    }
}
