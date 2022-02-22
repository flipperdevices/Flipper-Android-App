package com.flipperdevices.bridge.synchronization.impl.repository.android

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.core.ktx.jre.pmap
import java.math.BigInteger
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val MD5_LENGTH = 32
const val MD5_RADIX = 16
const val BIG_INTEGER_POSITIVE_NUMBER = 1

class AndroidHashRepository {
    suspend fun calculateHash(keys: List<FlipperKey>): List<KeyWithHash> {
        return keys.pmap { KeyWithHash(it.path, calculateHash(it)) }
    }

    private suspend fun calculateHash(key: FlipperKey): String = withContext(Dispatchers.IO) {
        val encoder = MessageDigest.getInstance("MD5")

        val md5Digest = key.keyContent.stream().use {
            encoder.digest(it.readBytes())
        }

        return@withContext BigInteger(BIG_INTEGER_POSITIVE_NUMBER, md5Digest)
            .toString(MD5_RADIX)
            .padStart(MD5_LENGTH, '0')
    }
}
