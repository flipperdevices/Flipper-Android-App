package com.flipperdevices.nfc.mfkey32.screen.model

import androidx.compose.runtime.Stable

sealed class MfKey32State {
    data class DownloadingRawFile(
        val percent: Float?
    ) : MfKey32State()

    data class Calculating(
        val percent: Float
    ) : MfKey32State()

    object Uploading : MfKey32State()
    data class Saved(val keys: List<String>) : MfKey32State()

    data class Error(val errorType: ErrorType) : MfKey32State()
}

@Stable
data class FoundedInformation(
    val keys: List<FoundedKey> = listOf(),
    val uniqueKeys: Set<String> = emptySet(),
    val duplicated: Map<String, DuplicatedSource> = emptyMap()
)

enum class DuplicatedSource {
    FLIPPER,
    USER
}

enum class ErrorType {
    NOT_FOUND_FILE,
    READ_WRITE,
    FLIPPER_CONNECTION
}

data class FoundedKey(
    val sectorName: String,
    val keyName: String,
    val key: String?
)
