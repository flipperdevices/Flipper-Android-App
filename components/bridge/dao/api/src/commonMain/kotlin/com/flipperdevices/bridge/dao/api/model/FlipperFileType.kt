package com.flipperdevices.bridge.dao.api.model

const val SHADOW_FILE_EXTENSION = "shd"
const val UI_INFRARED_EXTENSION = "irui"

/**
 * Order is important
 */
enum class FlipperFileType {
    KEY,
    SHADOW_NFC,
    OTHER,
    UI_INFRARED;

    companion object {
        fun getByExtension(extension: String): FlipperFileType {
            return when (extension) {
                SHADOW_FILE_EXTENSION -> SHADOW_NFC
                UI_INFRARED_EXTENSION -> UI_INFRARED
                else -> if (FlipperKeyType.getByExtension(extension) != null) {
                    KEY
                } else {
                    OTHER
                }
            }
        }
    }
}
