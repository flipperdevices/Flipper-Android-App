package com.flipperdevices.share.cryptostorage.helper

import android.security.keystore.KeyProperties
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.share.cryptostorage.ALGORITHM_HELPER
import com.flipperdevices.share.cryptostorage.BIT_SIZE
import com.flipperdevices.share.cryptostorage.IV_LENGTH
import com.flipperdevices.share.cryptostorage.TAG_LENGTH
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import java.io.File
import java.io.FileOutputStream
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class DecryptHelper {
    suspend fun writeDecrypt(
        inputStream: ByteReadChannel,
        tempFile: File,
        key: String
    ) {
        val decodedKey = Base64.getUrlDecoder().decode(key)
        val secretKey = SecretKeySpec(decodedKey, KeyProperties.KEY_ALGORITHM_AES)

        tempFile.outputStream().use { outputStream ->
            processDecrypt(inputStream, secretKey, outputStream)
        }
    }

    private suspend fun processDecrypt(
        inputStream: ByteReadChannel,
        secretKey: SecretKeySpec,
        outputStream: FileOutputStream
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        var cipher: Cipher? = null

        while (!inputStream.isClosedForRead) {
            val packet = inputStream.readRemaining()
            val bytes = packet.readByteArray()

            if (cipher == null) {
                val iv = bytes.copyOfRange(0, IV_LENGTH)
                val cipherText = bytes.copyOfRange(IV_LENGTH, bytes.size)
                cipher = Cipher.getInstance(ALGORITHM_HELPER).apply {
                    init(
                        Cipher.DECRYPT_MODE,
                        secretKey,
                        GCMParameterSpec(TAG_LENGTH * BIT_SIZE, iv)
                    )
                }
                val decryptedBytes = cipher.doFinal(cipherText)
                outputStream.write(decryptedBytes)
            } else {
                val decryptedBytes = cipher.doFinal(bytes)
                outputStream.write(decryptedBytes)
            }
        }
    }
}
