package com.flipperdevices.share.cryptostorage.helper

import android.security.keystore.KeyProperties
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.share.cryptostorage.ALGORITHM_HELPER
import com.flipperdevices.share.cryptostorage.BIT_SIZE
import com.flipperdevices.share.cryptostorage.IV_LENGTH
import com.flipperdevices.share.cryptostorage.KEY_SIZE
import com.flipperdevices.share.cryptostorage.TAG_LENGTH
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class EncryptHelper(
    private val flipperKeyContent: FlipperKeyContent
) {
    private val secretKey = KeyGenerator
        .getInstance(KeyProperties.KEY_ALGORITHM_AES)
        .apply { init(KEY_SIZE) }
        .generateKey()

    private val encryptionCipher = Cipher.getInstance(ALGORITHM_HELPER).apply {
        init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            GCMParameterSpec(TAG_LENGTH * BIT_SIZE, generateIV())
        )
    }

    suspend fun writeEncrypt(outputChannel: ByteWriteChannel) {
        val encryptBuffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val iv = encryptionCipher.iv
        outputChannel.writeFully(iv)

        var length: Int
        flipperKeyContent.openStream().use { stream ->
            while (stream.read(encryptBuffer).also { length = it } >= 0) {
                val encryptedBytes =
                    encryptionCipher.update(encryptBuffer, 0, length)
                outputChannel.writeFully(encryptedBytes)
            }
        }
        val encryptedBytes = encryptionCipher.doFinal()
        outputChannel.writeFully(encryptedBytes)
    }

    fun getKeyString(): String {
        return Base64
            .getUrlEncoder()
            .withoutPadding()
            .encodeToString(secretKey.encoded)
    }

    private fun generateIV(): ByteArray {
        val random = SecureRandom()
        val ivBytes = ByteArray(IV_LENGTH)
        random.nextBytes(ivBytes)
        return ivBytes
    }
}
