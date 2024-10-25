package com.flipperdevices.filemanager.editor.util

import com.flipperdevices.filemanager.editor.model.HexString
import java.nio.charset.Charset

object HexConverter {
    @OptIn(ExperimentalStdlibApi::class)
    fun toHexString(hexString: HexString): HexString {
        if (hexString is HexString.Hex) return hexString
        return hexString.content
            .toByteArray(Charset.defaultCharset())
            .toHexString(HexFormat.UpperCase)
            .let(HexString::Hex)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun fromHexString(hexString: HexString): HexString.Text {
        if (hexString is HexString.Text) return hexString
        return hexString.content
            .hexToByteArray(HexFormat.UpperCase)
            .decodeToString()
            .let(HexString::Text)
    }
}
