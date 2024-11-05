package com.flipperdevices.infrared.editor.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.core.test.readTestAssetString
import com.flipperdevices.infrared.editor.core.parser.InfraredKeyParser
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import kotlinx.collections.immutable.toImmutableList
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildKonfig.ROBOELECTRIC_SDK_VERSION])
class InfraredStateParserTest {

    @Test
    fun deleteSomeRemote() {
        val fff = FlipperFileFormat.fromFileContent(readTestAssetString("infrared.ir"))
        val currentFlipperKey = getFlipperKey(fff)

        val currentRemotes = InfraredKeyParser
            .mapParsedKeyToInfraredRemotes(fff)
            .toMutableList().apply {
                removeAt(1)
                removeAt(1)
            }

        val currentState = InfraredEditorState.Ready(
            remotes = currentRemotes.toImmutableList(),
            keyName = currentFlipperKey.path.nameWithoutExtension
        )

        val actualKey = InfraredStateParser.mapStateToFlipperKey(currentFlipperKey, currentState)
        val expectedKey = getFlipperKey(FlipperFileFormat.fromFileContent(readTestAssetString("infrared_deleted.ir")))

        Assert.assertEquals(actualKey, expectedKey)
    }

    private fun getFlipperKey(flipperFileFormat: FlipperFileFormat): FlipperKey {
        val notes = "notes"
        val flipperPath = FlipperFilePath(
            folder = "folder",
            nameWithExtension = "nameWithExtension.ext"
        )
        val flipperFile = FlipperFile(
            path = flipperPath,
            content = flipperFileFormat
        )
        return FlipperKey(
            notes = notes,
            synchronized = false,
            deleted = false,
            mainFile = flipperFile
        )
    }
}
