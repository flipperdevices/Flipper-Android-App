package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.model.FlipperFolderChanges
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.ManifestFile
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

private val TEST_TYPE = FlipperKeyType.NFC

class ManifestTimestampRepositoryTest {
    private lateinit var manifestStorage: ManifestStorage
    private lateinit var underTest: ManifestTimestampRepository

    @Before
    fun setUp() {
        manifestStorage = mockk()
        underTest = ManifestTimestampRepositoryImpl(manifestStorage)
    }

    @Test
    fun `update required when timestamp is larger`() = runTest {
        val testTimestampOlder = 1600000000000L
        val testTimestampFromFlipper = 1700000000000L
        coEvery { manifestStorage.load() } returns ManifestFile(
            keys = emptyList(),
            folderChanges = FlipperFolderChanges(
                mapOf(TEST_TYPE.flipperDir to testTimestampOlder)
            )
        )

        val actual = underTest.isUpdateRequired(TEST_TYPE, testTimestampFromFlipper)

        Assert.assertTrue(actual)
    }

    @Test
    fun `update required when timestamp is small`() = runTest {
        val testTimestampOlder = 1600000000000L
        val testTimestampFromFlipper = 1500000000000L
        coEvery { manifestStorage.load() } returns ManifestFile(
            keys = emptyList(),
            folderChanges = FlipperFolderChanges(
                mapOf(TEST_TYPE.flipperDir to testTimestampOlder)
            )
        )

        val actual = underTest.isUpdateRequired(TEST_TYPE, testTimestampFromFlipper)

        Assert.assertTrue(actual)
    }

    @Test
    fun `update not require when timestamp is equal`() = runTest {
        val testTimestampOlder = 1600000000000L
        val testTimestampFromFlipper = 1600000000000L
        coEvery { manifestStorage.load() } returns ManifestFile(
            keys = emptyList(),
            folderChanges = FlipperFolderChanges(
                mapOf(TEST_TYPE.flipperDir to testTimestampOlder)
            )
        )

        val actual = underTest.isUpdateRequired(TEST_TYPE, testTimestampFromFlipper)

        Assert.assertFalse(actual)
    }

    @Test
    fun `update require when timestamp not exist`() = runTest {
        val testTimestampFromFlipper = 1600000000000L
        coEvery { manifestStorage.load() } returns ManifestFile(
            keys = emptyList(),
            folderChanges = FlipperFolderChanges()
        )

        val actual = underTest.isUpdateRequired(TEST_TYPE, testTimestampFromFlipper)

        Assert.assertTrue(actual)
    }

    @Test
    fun `update require when manifest not exist`() = runTest {
        val testTimestampFromFlipper = 1600000000000L
        coEvery { manifestStorage.load() } returns null

        val actual = underTest.isUpdateRequired(TEST_TYPE, testTimestampFromFlipper)

        Assert.assertTrue(actual)
    }

    @Test
    fun `change timestamp on empty manifest`() = runTest {
        val testTimestampFromFlipper = 1600000000000L
        val updateFunctionSlot = slot<(ManifestFile) -> ManifestFile>()
        coEvery {
            manifestStorage.update(capture(updateFunctionSlot))
        } returns Unit

        underTest.setTimestampForType(TEST_TYPE, testTimestampFromFlipper)

        val actual = updateFunctionSlot.captured(ManifestFile(emptyList()))

        Assert.assertEquals(
            ManifestFile(
                keys = emptyList(),
                folderChanges = FlipperFolderChanges(
                    lastChangesTimestampMap = mapOf(TEST_TYPE.flipperDir to testTimestampFromFlipper)
                )
            ),
            actual
        )
    }

    @Test
    fun `change timestamp and not change manifest`() = runTest {
        val testKey = KeyWithHash(FlipperFilePath("test", "test.nfc"), "HASH")
        val testTimestampFromFlipper = 1600000000000L
        val updateFunctionSlot = slot<(ManifestFile) -> ManifestFile>()
        coEvery {
            manifestStorage.update(capture(updateFunctionSlot))
        } returns Unit

        underTest.setTimestampForType(TEST_TYPE, testTimestampFromFlipper)

        val actual = updateFunctionSlot.captured(ManifestFile(keys = listOf(testKey)))

        Assert.assertEquals(
            ManifestFile(
                keys = listOf(testKey),
                folderChanges = FlipperFolderChanges(
                    lastChangesTimestampMap = mapOf(TEST_TYPE.flipperDir to testTimestampFromFlipper)
                )
            ),
            actual
        )
    }
}
