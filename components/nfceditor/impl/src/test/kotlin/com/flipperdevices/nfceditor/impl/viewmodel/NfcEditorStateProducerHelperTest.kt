package com.flipperdevices.nfceditor.impl.viewmodel

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.test.readTestAsset
import com.flipperdevices.core.test.readTestAssetString
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.api.KeyParserImpl
import com.flipperdevices.nfceditor.impl.model.CardFieldInfo
import com.flipperdevices.nfceditor.impl.model.EditorField
import com.flipperdevices.nfceditor.impl.model.NfcCellType
import com.flipperdevices.nfceditor.impl.model.NfcEditorCardType
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class NfcEditorStateProducerHelperTest {
    @Test
    fun `1k apply color rules for each last line`() {
        val key = parseNfcKey("mf_1k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)
        editorState!!.sectors.forEach { sector ->
            Assert.assertArrayEquals(
                arrayOf(
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.ACCESS_BITS,
                    NfcCellType.ACCESS_BITS,
                    NfcCellType.ACCESS_BITS,
                    NfcCellType.SIMPLE,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B
                ),
                sector.lines.last().cells.map { it.cellType }.toTypedArray()
            )
        }
    }

    @Test
    fun `4k apply color rules for each last line`() {
        val key = parseNfcKey("mf_4k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)
        editorState!!.sectors.forEach { sector ->
            Assert.assertArrayEquals(
                arrayOf(
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.KEY_A,
                    NfcCellType.ACCESS_BITS,
                    NfcCellType.ACCESS_BITS,
                    NfcCellType.ACCESS_BITS,
                    NfcCellType.SIMPLE,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B,
                    NfcCellType.KEY_B
                ),
                sector.lines.last().cells.map { it.cellType }.toTypedArray()
            )
        }
    }

    @Test
    fun `1k apply color rule for first line`() {
        val key = parseNfcKey("mf_1k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)
        val sector = editorState!!.sectors.first()
        Assert.assertArrayEquals(
            Array(16) { NfcCellType.UID },
            sector.lines.first().cells.map { it.cellType }.toTypedArray()
        )
    }

    @Test
    fun `4k apply color rule for first line`() {
        val key = parseNfcKey("mf_4k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)
        val sector = editorState!!.sectors.first()
        Assert.assertArrayEquals(
            Array(16) { NfcCellType.UID },
            sector.lines.first().cells.map { it.cellType }.toTypedArray()
        )
    }

    @Test
    fun `1k correct parse atqa, uid, sak`() {
        val key = parseNfcKey("mf_1k_full.nfc")
        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        val cardInfo = editorState!!.nfcEditorCardInfo!!
        Assert.assertEquals(NfcEditorCardType.MF_1K, cardInfo.cardType)
        Assert.assertArrayEquals(
            arrayOf(
                NfcEditorCell("44", NfcCellType.SIMPLE),
                NfcEditorCell("00", NfcCellType.SIMPLE)
            ),
            cardInfo.fields[CardFieldInfo.ATQA].toTypedArray()
        )
        Assert.assertArrayEquals(
            arrayOf(
                NfcEditorCell("08", NfcCellType.SIMPLE)
            ),
            cardInfo.fields[CardFieldInfo.SAK].toTypedArray()
        )
        Assert.assertArrayEquals(
            arrayOf(
                NfcEditorCell("04", NfcCellType.SIMPLE),
                NfcEditorCell("77", NfcCellType.SIMPLE),
                NfcEditorCell("70", NfcCellType.SIMPLE),
                NfcEditorCell("2A", NfcCellType.SIMPLE),
                NfcEditorCell("23", NfcCellType.SIMPLE),
                NfcEditorCell("4F", NfcCellType.SIMPLE),
                NfcEditorCell("80", NfcCellType.SIMPLE)
            ),
            cardInfo.fields[CardFieldInfo.UID].toTypedArray()
        )
    }

    @Test
    fun `4k correct parse atqa, uid, sak`() {
        val key = parseNfcKey("mf_4k_full.nfc")
        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        val cardInfo = editorState!!.nfcEditorCardInfo!!
        Assert.assertEquals(NfcEditorCardType.MF_4K, cardInfo.cardType)
        Assert.assertArrayEquals(
            arrayOf(
                NfcEditorCell("42", NfcCellType.SIMPLE),
                NfcEditorCell("00", NfcCellType.SIMPLE)
            ),
            cardInfo.fields[CardFieldInfo.ATQA].toTypedArray()
        )
        Assert.assertArrayEquals(
            arrayOf(
                NfcEditorCell("18", NfcCellType.SIMPLE)
            ),
            cardInfo.fields[CardFieldInfo.SAK].toTypedArray()
        )
        Assert.assertArrayEquals(
            arrayOf(
                NfcEditorCell("04", NfcCellType.SIMPLE),
                NfcEditorCell("30", NfcCellType.SIMPLE),
                NfcEditorCell("68", NfcCellType.SIMPLE),
                NfcEditorCell("6A", NfcCellType.SIMPLE),
                NfcEditorCell("99", NfcCellType.SIMPLE),
                NfcEditorCell("66", NfcCellType.SIMPLE),
                NfcEditorCell("80", NfcCellType.SIMPLE)
            ),
            cardInfo.fields[CardFieldInfo.UID].toTypedArray()
        )
    }

    @Test
    fun `mf4k always has 40 sectors`() {
        val key = parseNfcKey("mf_4k_broke.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        Assert.assertEquals(40, editorState!!.sectors.size)
    }

    @Test
    fun `mf4k always has 40 sectors - overflow`() {
        val key = parseNfcKey("mf_4k_broke_overflow.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        Assert.assertEquals(40, editorState!!.sectors.size)
    }

    @Test
    fun `mf4k first sectors small`() {
        val key = parseNfcKey("mf_4k_broke.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        val sectors = editorState!!.sectors
        for (i in 0..31) {
            Assert.assertEquals(4, sectors[i].lines.size)
        }
        for (i in 32..39) {
            Assert.assertEquals(16, sectors[i].lines.size)
        }
    }

    @Test
    fun `mf4k first sectors large - overflow`() {
        val key = parseNfcKey("mf_4k_broke_overflow.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        val sectors = editorState!!.sectors
        for (i in 0..31) {
            Assert.assertEquals(4, sectors[i].lines.size)
        }
        for (i in 32..39) {
            Assert.assertEquals(16, sectors[i].lines.size)
        }
    }

    @Test
    fun `mf4k always has 256 lines`() {
        val key = parseNfcKey("mf_4k_broke.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        Assert.assertEquals(256, editorState!!.sectors.map { it.lines }.flatten().size)
    }

    @Test
    fun `mf4k always has 256 lines - overflow`() {
        val key = parseNfcKey("mf_4k_broke_overflow.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        Assert.assertEquals(256, editorState!!.sectors.map { it.lines }.flatten().size)
    }

    @Test
    fun `mf1k always has 16 sectors`() {
        val key = parseNfcKey("mf_1k_broke.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        Assert.assertEquals(16, editorState!!.sectors.size)
    }

    @Test
    fun `mf1k always has 16 sectors - overflow`() {
        val key = parseNfcKey("mf_1k_broke_overflow.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(key)
        Assert.assertNotNull(editorState)

        Assert.assertEquals(16, editorState!!.sectors.size)
    }

    @Test
    fun `parsed and produced file equal`() {
        val parsedKey = parseNfcKey("mf_4k_full.nfc", "mf_4k_full.shd")
        val key = getFlipperKey("mf_4k_full.nfc", "mf_4k_full.shd")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(parsedKey)!!
        val actualKey = NfcEditorStateProducerHelper.produceShadowFlipperKeyFromState(key, editorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.nfc"), actualMainContent)
        Assert.assertEquals(1, actualKey.additionalFiles.size)

        val actualShadowContent =
            actualKey.additionalFiles.first().content.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.shd"), actualShadowContent)
    }

    @Test
    fun `parsed and save dump file equal`() {
        val parsedKey = parseNfcKey("mf_4k_full.nfc", "mf_4k_full.shd")
        val key = getFlipperKey("mf_4k_full.nfc", "mf_4k_full.shd")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(parsedKey)!!
        val actualKey = NfcEditorStateProducerHelper.produceClearFlipperKeyFromState(key, editorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.shd"), actualMainContent)
        Assert.assertEquals(0, actualKey.additionalFiles.size)
    }

    @Test
    fun `always save shadow file - not edited`() {
        val key = getFlipperKey("mf_4k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
            parseNfcKey("mf_4k_full.nfc")
        )!!
        val actualKey = NfcEditorStateProducerHelper.produceShadowFlipperKeyFromState(key, editorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.nfc"), actualMainContent)
        Assert.assertEquals(1, actualKey.additionalFiles.size)

        val actualShadowContent =
            actualKey.additionalFiles.first().content.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.nfc"), actualShadowContent)
    }

    @Test
    fun `always save original file by dump - not edited`() {
        val key = getFlipperKey("mf_4k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
            parseNfcKey("mf_4k_full.nfc")
        )!!
        val actualKey = NfcEditorStateProducerHelper.produceClearFlipperKeyFromState(key, editorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.nfc"), actualMainContent)
        Assert.assertEquals(0, actualKey.additionalFiles.size)
    }

    @Test
    fun `always save shadow file - edited`() {
        val key = getFlipperKey("mf_4k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
            parseNfcKey("mf_4k_full.nfc")
        )!!
        val newEditorState = editorState.copyWithChangedContent(
            NfcEditorCellLocation(EditorField.DATA, 0, 1, 3),
            "AA"
        )
        val actualKey = NfcEditorStateProducerHelper.produceShadowFlipperKeyFromState(key, newEditorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.nfc"), actualMainContent)
        Assert.assertEquals(1, actualKey.additionalFiles.size)

        val actualShadowContent =
            actualKey.additionalFiles.first().content.openStream().use { it.readBytes() }
        Assert.assertEquals(
            readTestAssetString("mf_4k_full_edited.shd"),
            String(actualShadowContent)
        )
        Assert.assertArrayEquals(
            readTestAsset("mf_4k_full_edited.shd"),
            actualShadowContent
        )
    }

    @Test
    fun `always save original file by dump - edited`() {
        val key = getFlipperKey("mf_4k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
            parseNfcKey("mf_4k_full.nfc")
        )!!
        val newEditorState = editorState.copyWithChangedContent(
            NfcEditorCellLocation(EditorField.DATA, 0, 1, 3),
            "CC"
        )
        val actualKey = NfcEditorStateProducerHelper.produceClearFlipperKeyFromState(key, newEditorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full_edited.nfc"), actualMainContent)
        Assert.assertEquals(0, actualKey.additionalFiles.size)
    }

    @Test
    fun `always save shadow file - already exist`() {
        val key = getFlipperKey("mf_4k_full.nfc", "mf_4k_full.nfc")

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
            parseNfcKey("mf_4k_full.nfc", "mf_4k_full.nfc")
        )!!
        val newEditorState = editorState.copyWithChangedContent(
            NfcEditorCellLocation(EditorField.DATA, 0, 1, 3),
            "AA"
        )
        val actualKey = NfcEditorStateProducerHelper.produceShadowFlipperKeyFromState(key, newEditorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.nfc"), actualMainContent)
        Assert.assertEquals(1, actualKey.additionalFiles.size)
        val actualShadowContent =
            actualKey.additionalFiles.first().content.openStream().use { it.readBytes() }
        Assert.assertEquals(
            readTestAssetString("mf_4k_full_edited.shd"),
            String(actualShadowContent)
        )
        Assert.assertArrayEquals(
            readTestAsset("mf_4k_full_edited.shd"),
            actualShadowContent
        )
    }

    @Test
    fun `always save shadow file - many files`() {
        var key = getFlipperKey("mf_4k_full.nfc", "mf_4k_full.nfc")
        key = key.copy(
            additionalFiles = key.additionalFiles.plus(
                FlipperFile(
                    FlipperFilePath("test", "some_trash.txt"),
                    content = FlipperKeyContent.RawData(byteArrayOf())
                )
            ).toImmutableList()
        )

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
            parseNfcKey("mf_4k_full.nfc", "mf_4k_full.nfc")
        )!!
        val newEditorState = editorState.copyWithChangedContent(
            NfcEditorCellLocation(EditorField.DATA, 0, 1, 3),
            "AA"
        )
        val actualKey = NfcEditorStateProducerHelper.produceShadowFlipperKeyFromState(key, newEditorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full.nfc"), actualMainContent)
        Assert.assertEquals(2, actualKey.additionalFiles.size)
        val actualShadowContent = actualKey.additionalFiles
            .first { it.path.fileType == FlipperFileType.SHADOW_NFC }
            .content
            .openStream()
            .use { it.readBytes() }
        Assert.assertEquals(
            readTestAssetString("mf_4k_full_edited.shd"),
            String(actualShadowContent)
        )
        Assert.assertArrayEquals(
            readTestAsset("mf_4k_full_edited.shd"),
            actualShadowContent
        )
    }

    @Test
    fun `always save origianl file by dump - many files`() {
        var key = getFlipperKey("mf_4k_full.nfc", "mf_4k_full.nfc")
        key = key.copy(
            additionalFiles = key.additionalFiles.plus(
                FlipperFile(
                    FlipperFilePath("test", "some_trash.txt"),
                    content = FlipperKeyContent.RawData(byteArrayOf())
                )
            ).toImmutableList()
        )

        val editorState = NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
            parseNfcKey("mf_4k_full.nfc", "mf_4k_full.nfc")
        )!!
        val newEditorState = editorState.copyWithChangedContent(
            NfcEditorCellLocation(EditorField.DATA, 0, 1, 3),
            "CC"
        )
        val actualKey = NfcEditorStateProducerHelper.produceClearFlipperKeyFromState(key, newEditorState)

        val actualMainContent = actualKey.keyContent.openStream().use { it.readBytes() }
        Assert.assertArrayEquals(readTestAsset("mf_4k_full_edited.nfc"), actualMainContent)
        Assert.assertEquals(0, actualKey.additionalFiles.size)
    }
}

private fun getFlipperKey(
    path: String,
    shadowPath: String? = null
) = FlipperKey(
    mainFile = FlipperFile(
        path = FlipperFilePath("test", "test_file.nfc"),
        content = FlipperKeyContent.RawData(readTestAsset(path))
    ),
    additionalFiles = if (shadowPath != null) {
        listOf(
            FlipperFile(
                path = FlipperFilePath("test", "test_file.shd"),
                content = FlipperKeyContent.RawData(readTestAsset(shadowPath))
            )
        )
    } else {
        emptyList()
    }.toImmutableList(),
    synchronized = false,
    deleted = false
)

private fun parseNfcKey(
    path: String,
    shadowPath: String? = null
): FlipperKeyParsed.NFC = runBlocking {
    val keyParser = KeyParserImpl()
    val flipperKey = getFlipperKey(path, shadowPath)
    return@runBlocking keyParser.parseKey(flipperKey) as FlipperKeyParsed.NFC
}
