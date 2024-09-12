package com.flipperdevices.bridge.connection.feature.storage.api.fm

data class NameWithHash(
    val name: String,
    val md5: String,
    val size: Int,
    val type: FileType?
)

enum class FileType {
    FILE,
    DIR
}
