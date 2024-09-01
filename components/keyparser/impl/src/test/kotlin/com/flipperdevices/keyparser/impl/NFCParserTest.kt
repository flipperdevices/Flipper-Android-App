package com.flipperdevices.keyparser.impl

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.test.readTestAsset
import com.flipperdevices.core.test.readTestAssetString
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.impl.NFCParser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class NFCParserTest {
    private val underTest = NFCParser()

    @Test
    fun `use shadow file`() = runTest {
        val flipperKey = FlipperKey(
            mainFile = FlipperFile(
                path = FlipperFilePath("test", "test.nfc"),
                content = FlipperKeyContent.RawData(readTestAsset("mf_4k.nfc"))
            ),
            additionalFiles = persistentListOf(
                FlipperFile(
                    path = FlipperFilePath("test", "test.nfc.shd"),
                    content = FlipperKeyContent.RawData(readTestAsset("mf_4k.nfc.shd"))
                )
            ),
            synchronized = false,
            deleted = false
        )

        val parsedKey = underTest.parseKey(
            flipperKey,
            FlipperFileFormat.fromFileContent(readTestAssetString("mf_4k.nfc"))
        )

        Assert.assertTrue(parsedKey is FlipperKeyParsed.NFC)
        val nfcKey = parsedKey as FlipperKeyParsed.NFC
        Assert.assertEquals("42 11", nfcKey.atqa)
        Assert.assertEquals(
            "00 00 00 00 80 08 50 00 00 00 00 00 00 00 00 00",
            nfcKey.lines[1].second
        )
        Assert.assertEquals(
            1,
            nfcKey.lines[1].first
        )
    }

    @Test
    fun `shadow file invalid`() = runTest {
        val flipperKey = FlipperKey(
            mainFile = FlipperFile(
                path = FlipperFilePath("test", "test.nfc"),
                content = FlipperKeyContent.RawData(readTestAsset("mf_4k.nfc"))
            ),
            additionalFiles = persistentListOf(
                FlipperFile(
                    path = FlipperFilePath("test", "test.nfc.invalidshd"),
                    content = FlipperKeyContent.RawData(readTestAsset("mf_4k.nfc.shd"))
                )
            ),
            synchronized = false,
            deleted = false
        )

        val parsedKey = underTest.parseKey(
            flipperKey,
            FlipperFileFormat.fromFileContent(readTestAssetString("mf_4k.nfc"))
        )

        Assert.assertTrue(parsedKey is FlipperKeyParsed.NFC)
        val nfcKey = parsedKey as FlipperKeyParsed.NFC
        Assert.assertEquals("42 00", nfcKey.atqa)
        Assert.assertEquals(
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            nfcKey.lines[1].second
        )
        Assert.assertEquals(
            1,
            nfcKey.lines[1].first
        )
    }

    @Test
    fun `shadow file not exist`() = runTest {
        val flipperKey = FlipperKey(
            mainFile = FlipperFile(
                path = FlipperFilePath("test", "test.nfc"),
                content = FlipperKeyContent.RawData(readTestAsset("mf_4k.nfc"))
            ),
            synchronized = false,
            deleted = false
        )

        val parsedKey = underTest.parseKey(
            flipperKey,
            FlipperFileFormat.fromFileContent(readTestAssetString("mf_4k.nfc"))
        )

        Assert.assertTrue(parsedKey is FlipperKeyParsed.NFC)
        val nfcKey = parsedKey as FlipperKeyParsed.NFC
        Assert.assertEquals("42 00", nfcKey.atqa)
        Assert.assertEquals(
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            nfcKey.lines[1].second
        )
        Assert.assertEquals(
            1,
            nfcKey.lines[1].first
        )
    }
}
