package com.flipperdevices.filemanager.editor.model

sealed interface HexString {
    val content: String
    val type: EditorEncodingEnum

    data class Text(override val content: String) : HexString {
        override val type: EditorEncodingEnum = EditorEncodingEnum.TEXT
    }

    data class Hex(override val content: String) : HexString {
        override val type: EditorEncodingEnum = EditorEncodingEnum.HEX
    }
}
