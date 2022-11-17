package com.flipperdevices.share.cryptostorage.helper

import android.security.keystore.KeyProperties
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.cryptostorage.model.EncryptData
import com.squareup.anvil.annotations.ContributesBinding
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

private const val ALGORITHM_HELPER = "AES/GCM/NoPadding"
private const val KEY_SIZE = 192
private const val TAG_LENGTH = 16
private const val BIT_SIZE = 8
private const val IV_LENGTH = 12

interface CryptoHelperApi {
    fun encrypt(data: ByteArray): EncryptData
    fun decrypt(data: ByteArray, key: String): ByteArray
}

@ContributesBinding(AppGraph::class)
class CryptoHelperApiImpl @Inject constructor() : CryptoHelperApi {
    override fun encrypt(data: ByteArray): EncryptData {
        val generator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES)
            .apply { init(KEY_SIZE) }
        val secretKey = generator.generateKey()
        val keyString = Base64.getEncoder().encodeToString(secretKey.encoded)

        val encryptionCipher = Cipher.getInstance(ALGORITHM_HELPER).apply {
            init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                GCMParameterSpec(TAG_LENGTH * BIT_SIZE, generateIV())
            )
        }
        val iv = encryptionCipher.iv
        val encryptedBytes = encryptionCipher.doFinal(data)
        return EncryptData(key = keyString, data = iv + encryptedBytes)
    }

    override fun decrypt(data: ByteArray, key: String): ByteArray {
        val decodedKey: ByteArray = Base64.getDecoder().decode(key)
        val secretKey = SecretKeySpec(decodedKey, KeyProperties.KEY_ALGORITHM_AES)

        val iv = data.copyOfRange(0, IV_LENGTH)
        val cipherText = data.copyOfRange(IV_LENGTH, data.size)

        val decryptCipher = Cipher.getInstance(ALGORITHM_HELPER).apply {
            init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH * BIT_SIZE, iv))
        }
        return decryptCipher.doFinal(cipherText)
    }

    private fun generateIV(): ByteArray {
        val random = SecureRandom()
        val ivBytes = ByteArray(IV_LENGTH)
        random.nextBytes(ivBytes)
        return ivBytes
    }
}
