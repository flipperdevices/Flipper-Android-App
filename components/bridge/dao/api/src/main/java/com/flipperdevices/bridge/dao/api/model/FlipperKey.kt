package com.flipperdevices.bridge.dao.api.model

data class FlipperKey(
    val name: String,
    val fileType: FlipperFileType
) {
    companion object {
        val DUMMY = FlipperKey("Test Key", FlipperFileType.NFC)
        val DUMMY_LIST: List<FlipperKey>
            get() = FlipperFileType.values().map { fileType ->
                FlipperKey("${fileType.humanReadableName} Key", fileType)
            }
    }
}
