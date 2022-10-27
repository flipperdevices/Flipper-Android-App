package com.flipperdevices.nfc.mfkey32.screen.model

import androidx.compose.runtime.Stable
import com.flipperdevices.core.preference.pb.HardwareColor

sealed class MfKey32State {
    data class DownloadingRawFile(
        val percent: Float?
    ) : MfKey32State()

    data class Calculating(
        val percent: Float
    ) : MfKey32State()

    object Uploading : MfKey32State()
    class Saved(val keys: List<String>) : MfKey32State()

    data class Error(val color: HardwareColor) : MfKey32State()
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

data class FoundedKey(
    val sectorName: String,
    val keyName: String,
    val key: String?
)
