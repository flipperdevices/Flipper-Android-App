package com.flipperdevices.bridge.dao.api.model

data class FlipperKey(
    val name: String,
    val fileType: FlipperFileType,
    val keyType: FlipperKeyType
) {
    companion object {
        val DUMMY = FlipperKey("Test Key", FlipperFileType.NFC, FlipperKeyType())
        val DUMMY_LIST: List<FlipperKey>
            get() = FlipperFileType.values().map { fileType ->
                FlipperKey("${fileType.humanReadableName} Key", fileType, FlipperKeyType("Unknown"))
            }
    }
}
