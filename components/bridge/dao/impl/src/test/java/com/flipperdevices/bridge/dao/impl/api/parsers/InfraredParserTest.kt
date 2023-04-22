@file:Suppress("MaxLineLength")

package com.flipperdevices.bridge.dao.impl.api.parsers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.infrared.InfraredControl
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.test.readTestAsset
import com.flipperdevices.core.test.readTestAssetString
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InfraredParserTest {
    private val underTest = InfraredParser()

    @Test
    fun sample() = runTest {
        val flipperKey = FlipperKey(
            mainFile = FlipperFile(
                path = FlipperFilePath("test", "example.ir"),
                content = FlipperKeyContent.RawData(readTestAsset("example.ir"))
            ),
            synchronized = false,
            deleted = false
        )

        val parsedKey = underTest.parseKey(
            flipperKey,
            FlipperFileFormat.fromFileContent(readTestAssetString("example.ir"))
        )

        val listRemotes = listOf(
            InfraredControl.Parsed(
                nameInternal = "Button_1",
                protocol = "NECext",
                address = "EE 87 00 00",
                command = "5D A0 00 00",
            ),
            InfraredControl.Raw(
                nameInternal = "Button_2",
                frequency = "38000",
                dutyCycle = "0.330000",
                data = "504 3432 502 483 500 484 510 502 502 482 501 485 509 1452 504 1458 509 1452 504 481 501 474 509 3420 503"
            ),
            InfraredControl.Parsed(
                nameInternal = "Button_3",
                protocol = "SIRC",
                address = "01 00 00 00",
                command = "15 00 00 00",
            ),
        )

        Assert.assertTrue(parsedKey is FlipperKeyParsed.Infrared)

        val parsedInfrared = parsedKey as FlipperKeyParsed.Infrared
        listRemotes.forEachIndexed { index, item ->
            val remote = parsedInfrared.remotes[index]
            Assert.assertEquals(item, remote)
        }

        Assert.assertEquals("example", parsedInfrared.keyName)
    }
}
