package com.flipperdevices.share.cryptostorage.helper

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.cryptostorage.model.EncryptData
import com.squareup.anvil.annotations.ContributesBinding
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

private const val ALGORITHM = "AES"
private const val ALGORITHM_HELPER = "AES/GCM/NoPadding"
private const val KEY_SIZE = 128
private const val GCM_SIZE = 128
private const val GCM_VECTOR_SIZE = 12

interface CryptoHelperApi {
    fun encrypt(data: ByteArray): EncryptData
    fun decrypt(data: ByteArray, key: String): ByteArray
}

@ContributesBinding(AppGraph::class)
class CryptoHelperApiImpl @Inject constructor() : CryptoHelperApi {
    private fun gcm() = GCMParameterSpec(GCM_SIZE, ByteArray(GCM_VECTOR_SIZE) { 0 })
    override fun encrypt(data: ByteArray): EncryptData {
        val generator = KeyGenerator.getInstance(ALGORITHM).apply {
            init(KEY_SIZE)
        }
        val secretKey = generator.generateKey()
        val encryptionCipher = Cipher.getInstance(ALGORITHM_HELPER).apply {
            init(Cipher.ENCRYPT_MODE, secretKey, gcm())
        }
        val encryptedBytes = encryptionCipher.doFinal(data)
        val keyString = Base64.getEncoder().encodeToString(secretKey.encoded)
        return EncryptData(key = keyString, data = encryptedBytes)
    }

    override fun decrypt(data: ByteArray, key: String): ByteArray {
        val decodedKey: ByteArray = Base64.getDecoder().decode(key)
        val secretKey = SecretKeySpec(decodedKey, ALGORITHM)
        val decryptCipher = Cipher.getInstance(ALGORITHM_HELPER).apply {
            init(Cipher.DECRYPT_MODE, secretKey, gcm())
        }
        return decryptCipher.doFinal(data)
    }
}
