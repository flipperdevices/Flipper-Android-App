package com.flipperdevices.share.cryptostorage

import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

private const val ALGORITHM = "AES"
private const val ALGORITHM_HELPER = "AES/GCM/NoPadding"
private const val KEY_SIZE = 128

interface CryptoHelperApi {
    fun encrypt(data: ByteArray): Pair<String, ByteArray>
    fun decode(data: ByteArray, key: String): ByteArray
}

@ContributesBinding(AppGraph::class)
class CryptoHelperApiImpl @Inject constructor() : CryptoHelperApi {
    override fun encrypt(data: ByteArray): Pair<String, ByteArray> {
        val generator = KeyGenerator.getInstance(ALGORITHM).apply {
            init(KEY_SIZE)
        }
        val secretKey = generator.generateKey()
        val encryptionCipher = Cipher.getInstance(ALGORITHM_HELPER).apply {
            init(Cipher.ENCRYPT_MODE, secretKey, generateIv())
        }
        val encryptedBytes = encryptionCipher.doFinal(data)
        val keyString = Base64.getEncoder().encodeToString(secretKey.encoded)
        return keyString to encryptedBytes
    }

    override fun decode(data: ByteArray, key: String): ByteArray {
        val decodedKey: ByteArray = Base64.getDecoder().decode(key)
        val secretKey = SecretKeySpec(decodedKey, ALGORITHM)
        val decryptCipher = Cipher.getInstance(ALGORITHM_HELPER).apply {
            init(Cipher.DECRYPT_MODE, secretKey, generateIv())
        }
        return decryptCipher.doFinal(data)
    }

    fun generateIv(): IvParameterSpec {
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        return IvParameterSpec(iv)
    }
}
