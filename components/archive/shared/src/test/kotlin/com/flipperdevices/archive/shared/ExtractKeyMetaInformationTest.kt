package com.flipperdevices.archive.shared

import com.flipperdevices.archive.shared.utils.ExtractKeyMetaInformation
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ExtractKeyMetaInformationTest(
    private val keyParsed: FlipperKeyParsed,
    private val metaInformation: String?
) {
    @Test
    fun `Correct meta information`() {
        val information = ExtractKeyMetaInformation.extractProtocol(keyParsed)
        Assert.assertEquals(information, metaInformation)
    }

    companion object {
        @JvmStatic
        @Suppress("LongMethod")
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(
                FlipperKeyParsed.IButton(
                    keyName = "IButton",
                    notes = null,
                    keyType = "IButton",
                    data = null
                ),
                "IButton"
            ),
            arrayOf(
                FlipperKeyParsed.Infrared(
                    keyName = "Infrared",
                    notes = null,
                    protocol = "TV",
                    remotes = listOf()
                ),
                "TV"
            ),
            arrayOf(
                FlipperKeyParsed.NFC(
                    keyName = "NFC",
                    notes = null,
                    deviceType = "MiFare",
                    uid = null,
                    version = 1,
                    atqa = null,
                    sak = null,
                    mifareClassicType = null,
                    dataFormatVersion = 3,
                    lines = listOf()
                ),
                "MiFare"
            ),
            arrayOf(
                FlipperKeyParsed.RFID(
                    keyName = "RFID",
                    notes = null,
                    data = null,
                    keyType = "125"
                ),
                "125"
            ),
            arrayOf(
                FlipperKeyParsed.SubGhz(
                    keyName = "SubGhz",
                    notes = null,
                    protocol = "432",
                    key = null
                ),
                "432"
            ),
            arrayOf(
                FlipperKeyParsed.Unrecognized(
                    keyName = "Unrecognized",
                    notes = null,
                    fileType = null,
                    orderedDict = persistentListOf()
                ),
                null
            )
        )
    }
}
