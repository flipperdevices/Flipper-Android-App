package com.flipperdevices.share.cryptostorage

import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.share.cryptostorage.helper.DecryptHelper
import com.flipperdevices.share.cryptostorage.helper.EncryptHelper
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert
import org.junit.Test
import java.io.File

class CryptoTest {

    @Test
    fun simpleCryptoTest() = runTest {
        val originalData = "I Love Flipper".toByteArray()
        val flipperKeyContent = FlipperKeyContent.RawData(originalData)

        // Encrypt
        val encryptHelper = EncryptHelper(flipperKeyContent)
        val encryptedChannel = ByteChannel(autoFlush = true)
        encryptHelper.writeEncrypt(encryptedChannel)
        encryptedChannel.close()

        val encryptedBytes = encryptedChannel.toByteArray()
        val encryptedReadChannel = ByteReadChannel(encryptedBytes)

        // Decrypt
        val decryptHelper = DecryptHelper()
        val tempFile = withContext(FlipperDispatchers.workStealingDispatcher) {
            File.createTempFile("temp", null)
        }
        decryptHelper.writeDecrypt(encryptedReadChannel, tempFile, encryptHelper.getKeyString())

        val decryptedStream = tempFile.inputStream()
        val decryptedData = decryptedStream.readBytes()

        Assert.assertArrayEquals(originalData, decryptedData)
    }
}
