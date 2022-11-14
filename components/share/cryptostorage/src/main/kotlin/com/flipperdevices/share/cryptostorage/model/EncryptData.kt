package com.flipperdevices.share.cryptostorage.model

data class EncryptData(
    val data: ByteArray,
    val key: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptData

        if (!data.contentEquals(other.data)) return false
        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + key.hashCode()
        return result
    }
}
