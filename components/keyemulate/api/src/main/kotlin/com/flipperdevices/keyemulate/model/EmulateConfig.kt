package com.flipperdevices.keyemulate.model

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType

/*
    Override equals and hashCode to compare emulate key, because emulate time not important
 */
data class EmulateConfig(
    val keyType: FlipperKeyType,
    val keyPath: FlipperFilePath,
    val minEmulateTime: Long? = null,
    val args: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmulateConfig

        if (keyType != other.keyType) return false
        if (keyPath != other.keyPath) return false
        if (args != other.args) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keyType.hashCode()
        result = 31 * result + keyPath.hashCode()
        result = 31 * result + (args?.hashCode() ?: 0)
        return result
    }
}
