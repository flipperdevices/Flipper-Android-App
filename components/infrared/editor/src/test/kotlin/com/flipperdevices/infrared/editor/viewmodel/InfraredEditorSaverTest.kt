@file:Suppress("MaxLineLength")

package com.flipperdevices.infrared.editor.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.infrared.InfraredRemote
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert
import org.junit.Test

class InfraredEditorSaverTest {
    private val underTest = InfraredEditorSaver()

    private val flipperContent = FlipperFileFormat(
        orderedDict = listOf(
            "Filetype" to "IR signals file",
            "Version" to "1",
            "name" to "Button_1",
            "type" to "parsed",
            "protocol" to "NECext",
            "address" to "EE 87 00 00",
            "command" to "5D A0 00 00",
            "name" to "Button_2",
            "type" to "raw",
            "frequency" to "38000",
            "duty_cycle" to "0.330000",
            "data" to "504 3432 502 483 500 484 510 502 502 482 501 485 509 1452 504 1458 509 1452 504 481 501 474 509 3420 503",
            "name" to "Button_3",
            "type" to "parsed",
            "protocol" to "SIRC",
            "address" to "01 00 00 00",
            "command" to "15 00 00 00",

        )
    )

    private val path = mockk<FlipperFilePath>()
    private val oldKey = FlipperKey(
        mainFile = FlipperFile(
            path = path,
            content = flipperContent
        ),
        additionalFiles = listOf(),
        notes = "",
        synchronized = false,
        deleted = false
    )

    private val remotes = persistentListOf(
        InfraredRemote.Raw(
            nameInternal = "Button_2",
            frequency = "38000",
            dutyCycle = "0.330000",
            data = "504 3432 502 483 500 484 510 502 502 482 501 485 509 1452 504 1458 509 1452 504 481 501 474 509 3420 503"
        ),
        InfraredRemote.Parsed(
            nameInternal = "Button_1",
            protocol = "NECext",
            address = "EE 87 00 00",
            command = "5D A0 00 00"
        ),
        InfraredRemote.Parsed(
            nameInternal = "Button_3",
            protocol = "SIRC",
            address = "01 00 00 00",
            command = "15 00 00 00"
        ),
    )

    private val changesFlipperContent = FlipperFileFormat(
        orderedDict = listOf(
            "Filetype" to "IR signals file",
            "Version" to "1",
            "name" to "Button_2",
            "type" to "raw",
            "frequency" to "38000",
            "duty_cycle" to "0.330000",
            "data" to "504 3432 502 483 500 484 510 502 502 482 501 485 509 1452 504 1458 509 1452 504 481 501 474 509 3420 503",
            "name" to "Button_1",
            "type" to "parsed",
            "protocol" to "NECext",
            "address" to "EE 87 00 00",
            "command" to "5D A0 00 00",
            "name" to "Button_3",
            "type" to "parsed",
            "protocol" to "SIRC",
            "address" to "01 00 00 00",
            "command" to "15 00 00 00",
        )
    )

    @Test fun sampleTest() {
        val newKey = underTest.newFlipperKey(oldKey, remotes)
        val newContent = newKey.mainFile.content

        Assert.assertEquals(changesFlipperContent, newContent)
    }
}
