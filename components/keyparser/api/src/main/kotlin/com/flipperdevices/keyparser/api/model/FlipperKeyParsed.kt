package com.flipperdevices.keyparser.api.model

import androidx.compose.runtime.Immutable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface FlipperKeyParsed {
    val keyName: String
    val notes: String?
    val fileType: FlipperKeyType?

    data class Infrared(
        override val keyName: String,
        override val notes: String?,
        val protocol: String?,
        val remotes: List<String>
    ) : FlipperKeyParsed {
        override val fileType: FlipperKeyType = FlipperKeyType.INFRARED
    }

    @Suppress("LongParameterList")
    data class NFC(
        override val keyName: String,
        override val notes: String?,
        val deviceType: String?,
        val uid: String?,
        val version: Int,
        val atqa: String?,
        val sak: String?,
        val mifareClassicType: String?,
        val dataFormatVersion: Int,
        val lines: List<Pair<Int, String>>
    ) : FlipperKeyParsed {
        override val fileType: FlipperKeyType = FlipperKeyType.NFC
    }

    data class SubGhz(
        override val keyName: String,
        override val notes: String?,
        val protocol: String?,
        val key: String?,
        val totalTimeMs: Long? = null
    ) : FlipperKeyParsed {
        override val fileType: FlipperKeyType = FlipperKeyType.SUB_GHZ
    }

    data class IButton(
        override val keyName: String,
        override val notes: String?,
        val keyType: String?,
        val data: String?
    ) : FlipperKeyParsed {
        override val fileType: FlipperKeyType = FlipperKeyType.I_BUTTON
    }

    data class RFID(
        override val keyName: String,
        override val notes: String?,
        val data: String?,
        val keyType: String?
    ) : FlipperKeyParsed {
        override val fileType = FlipperKeyType.RFID
    }

    class Unrecognized(
        override val keyName: String,
        override val notes: String?,
        override val fileType: FlipperKeyType?,
        val orderedDict: ImmutableList<Pair<String, String>>
    ) : FlipperKeyParsed
}
