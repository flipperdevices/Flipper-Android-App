package com.flipperdevices.infrared.editor.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.core.test.readTestAssetString
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.infrared.editor.core.parser.InfraredKeyParser
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildKonfig.ROBOELECTRIC_SDK_VERSION])
class InfraredKeyParserTest {

    @Test
    @Suppress("MaximumLineLength", "MaxLineLength")
    fun mixedInfraredRemote() {
        val fff = FlipperFileFormat.fromFileContent(readTestAssetString("infrared.ir"))
        val actualRemote = InfraredKeyParser.mapParsedKeyToInfraredRemotes(fff)

        val expectedRemote = listOf(
            InfraredRemote.Raw(
                nameInternal = "Heat_lo",
                typeInternal = "raw",
                frequency = "38000",
                dutyCycle = "0.330000",
                data = "123 456"
            ),
            InfraredRemote.Raw(
                nameInternal = "Dh",
                typeInternal = "raw",
                frequency = "38000",
                dutyCycle = "0.330000",
                data = "456 123"
            ),
            InfraredRemote.Parsed(
                nameInternal = "Mute",
                typeInternal = "parsed",
                protocol = "NEC",
                address = "77 00 00 00",
                command = "F3 00 00 00",
            ),
            InfraredRemote.Parsed(
                nameInternal = "Vol_up",
                typeInternal = "parsed",
                protocol = "NEC",
                address = "77 00 00 00",
                command = "FB 00 00 00",
            )
        )
        Assert.assertArrayEquals(actualRemote.toTypedArray(), expectedRemote.toTypedArray())
    }
}
