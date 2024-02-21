package com.flipperdevices.share.receive.viewmodels

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.receive.helpers.FlipperKeyParserHelper
import com.flipperdevices.share.receive.models.FlipperKeyParseException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FlipperKeyParserHelperTest {
    private lateinit var cryptoStorageApi: CryptoStorageApi
    private lateinit var flipperKeyParserHelper: FlipperKeyParserHelper

    @Before
    fun setUp() {
        cryptoStorageApi = mockk()
        flipperKeyParserHelper = FlipperKeyParserHelper(cryptoStorageApi)
    }

    @Test
    fun `Nullable Deeplink`() = runTest {
        // Initialize
        val deeplink: Deeplink.RootLevel.SaveKey? = null

        // Actions
        val resultParse = flipperKeyParserHelper.toFlipperKey(deeplink)

        // Assertions
        Assert.assertTrue(resultParse.isFailure)
    }

    @Test
    fun `Flipper key deeplink with null content`() = runTest {
        // Initialize
        val deeplink = Deeplink.RootLevel.SaveKey.FlipperKey(
            path = mockk(),
            content = null
        )

        // Actions
        val resultParse = flipperKeyParserHelper.toFlipperKey(deeplink)
        val exception = resultParse.exceptionOrNull()

        // Assertions
        Assert.assertTrue(resultParse.isFailure)
        when (exception) {
            null -> Assert.fail("Exception is null")
            else -> Assert.assertTrue(exception is FlipperKeyParseException)
        }
    }

    @Test
    fun `Flipper key deeplink with external uri content`() = runTest {
        // Initialize
        val content = DeeplinkContent.ExternalUri("", null, "")
        val deeplink = Deeplink.RootLevel.SaveKey.FlipperKey(
            path = mockk(),
            content = content
        )

        // Actions
        val resultParse = flipperKeyParserHelper.toFlipperKey(deeplink)
        val exception = resultParse.exceptionOrNull()

        // Assertions
        Assert.assertTrue(resultParse.isFailure)
        when (exception) {
            null -> Assert.fail("Exception is null")
            else -> Assert.assertTrue(exception is FlipperKeyParseException)
        }
    }

    @Test
    fun `Flipper key deeplink with FFF content`() = runTest {
        // Initialize
        val path = FlipperFilePath(
            folder = "test",
            nameWithExtension = "test.test"
        )
        val fff = FlipperFileFormat(listOf())
        val content = DeeplinkContent.FFFContent(
            filename = "test",
            flipperFileFormat = fff
        )
        val deeplink = Deeplink.RootLevel.SaveKey.FlipperKey(
            path = path,
            content = content
        )

        // Actions
        val resultParse = flipperKeyParserHelper.toFlipperKey(deeplink)
        val flipperKey = resultParse.getOrNull()

        // Assertions
        Assert.assertTrue(resultParse.isSuccess)
        when (flipperKey) {
            null -> Assert.fail("Exception is null")
            else -> {
                val flipperKeyFile = flipperKey.mainFile

                Assert.assertEquals(path, flipperKey.path)
                Assert.assertEquals(false, flipperKey.synchronized)
                Assert.assertEquals(false, flipperKey.deleted)
                Assert.assertEquals(fff, flipperKeyFile.content)
                Assert.assertEquals(path, flipperKeyFile.path)
            }
        }
    }

    @Test
    fun `Flipper key deeplink with internal storage content`() = runTest {
        // Initialize
        val content = DeeplinkContent.InternalStorageFile(filePath = "test/test.test")
        val deeplink = Deeplink.RootLevel.SaveKey.FlipperKey(
            path = mockk(),
            content = content
        )

        // Actions
        val resultParse = flipperKeyParserHelper.toFlipperKey(deeplink)
        val flipperKey = resultParse.getOrNull()

        // Assertions
        Assert.assertTrue(resultParse.isSuccess)
        when (flipperKey) {
            null -> Assert.fail("Exception is null")
            else -> {
                Assert.assertEquals(FlipperFilePath("test", "test.test"), flipperKey.path)
                Assert.assertEquals(false, flipperKey.synchronized)
                Assert.assertEquals(false, flipperKey.deleted)

                val flipperKeyFile = flipperKey.mainFile
                Assert.assertEquals("test.test", flipperKeyFile.path.nameWithExtension)
                Assert.assertEquals("test", flipperKeyFile.path.extension)
            }
        }
    }

    @Test
    fun `Flipper key deeplink with crypto storage content`() = runTest {
        // Initialize
        val rawData = ByteArray(5) { 1 }
        val cryptoKey = FlipperKeyCrypto(fileId = "1", pathToKey = "text/test.test", "key")
        val content = DeeplinkContent.FFFCryptoContent(key = cryptoKey)

        val path = FlipperFilePath(
            folder = "test",
            nameWithExtension = "test.test"
        )

        val deeplink = Deeplink.RootLevel.SaveKey.FlipperKey(
            path = path,
            content = content
        )

        // Mock
        coEvery {
            cryptoStorageApi.download(
                id = cryptoKey.fileId,
                key = cryptoKey.cryptoKey,
                name = "test.test"
            )
        } returns Result.success(FlipperKeyContent.RawData(rawData))

        // Actions
        val resultParse = flipperKeyParserHelper.toFlipperKey(deeplink)
        val flipperKey = resultParse.getOrNull()

        // Assertions
        Assert.assertTrue(resultParse.isSuccess)
        when (flipperKey) {
            null -> Assert.fail("Exception is null")
            else -> {
                Assert.assertEquals(FlipperFilePath("test", "test.test"), flipperKey.path)
                Assert.assertEquals(false, flipperKey.synchronized)
                Assert.assertEquals(false, flipperKey.deleted)

                val flipperKeyFile = flipperKey.mainFile
                Assert.assertEquals("test.test", flipperKeyFile.path.nameWithExtension)
                Assert.assertEquals("test", flipperKeyFile.path.extension)
            }
        }
    }

    @Test
    fun `Flipper key deeplink with crypto storage content and exception`() = runTest {
        // Initialize
        val cryptoKey = FlipperKeyCrypto(fileId = "1", pathToKey = "text/test.test", "key")
        val content = DeeplinkContent.FFFCryptoContent(key = cryptoKey)

        val path = FlipperFilePath(
            folder = "test",
            nameWithExtension = "test.test"
        )

        val deeplink = Deeplink.RootLevel.SaveKey.FlipperKey(
            path = path,
            content = content
        )

        // Mock
        coEvery {
            cryptoStorageApi.download(
                id = cryptoKey.fileId,
                key = cryptoKey.cryptoKey,
                name = "test.test"
            )
        } returns Result.failure(Exception())

        // Actions
        val resultParse = flipperKeyParserHelper.toFlipperKey(deeplink)
        val exception = resultParse.exceptionOrNull()

        // Assertions
        Assert.assertTrue(resultParse.isFailure)
        when (exception) {
            null -> Assert.fail("Exception is null")
            else -> Assert.assertTrue(exception is java.lang.Exception)
        }
    }
}
