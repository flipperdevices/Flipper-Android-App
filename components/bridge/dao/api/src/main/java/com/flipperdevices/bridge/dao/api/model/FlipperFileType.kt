package com.flipperdevices.bridge.dao.api.model

const val SHADOW_FILE_EXTENSION = "shd"

/**
 * Order is important
 */
enum class FlipperFileType {
    KEY,
    SHADOW_NFC,
    OTHER;

    companion object {
        fun getByExtension(extension: String): FlipperFileType {
            return when (extension) {
                SHADOW_FILE_EXTENSION -> SHADOW_NFC
                else -> if (FlipperKeyType.getByExtension(extension) != null) {
                    KEY
                } else {
                    OTHER
                }
            }
        }
    }
}
