package com.flipperdevices.remotecontrols.impl.setup.encoding

import java.security.MessageDigest

internal class JvmEncoder(override val algorithm: ByteArrayEncoder.Algorithm) : ByteArrayEncoder {
    @OptIn(ExperimentalStdlibApi::class)
    override fun encode(byteArray: ByteArray): String {
        val algorithmName = when (algorithm) {
            ByteArrayEncoder.Algorithm.MD5 -> "MD5"
            ByteArrayEncoder.Algorithm.SHA_256 -> "SHA-256"
        }
        return MessageDigest.getInstance(algorithmName)
            .digest(byteArray)
            .toHexString()
    }
}
