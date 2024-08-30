package com.flipperdevices.bridge.dao.impl.api.key

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.preference.FlipperStorageProvider
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private val EXAMPLE_FLIPPER_KEY = FlipperKey(
    mainFile = FlipperFile(
        path = FlipperFilePath(
            folder = "test",
            nameWithExtension = "key.nfc"
        ),
        content = FlipperKeyContent.RawData("Hello, world".toByteArray())
    ),
    additionalFiles = persistentListOf(
        FlipperFile(
            path = FlipperFilePath(
                folder = "test",
                nameWithExtension = "additional_file.nfc"
            ),
            content = FlipperKeyContent.RawData("Hello from additional file".toByteArray())
        )
    ),
    synchronized = false,
    deleted = false,
    notes = null
)

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class ShouldSynchronizeHelperTest {
    @Test
    fun `edit name of key`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            mainFile = FlipperFile(
                path = FlipperFilePath(
                    EXAMPLE_FLIPPER_KEY.path.folder,
                    "newName.nfc"
                ),
                content = EXAMPLE_FLIPPER_KEY.mainFile.content
            )
        )

        Assert.assertTrue(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `edit name of key equals`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            mainFile = FlipperFile(
                path = FlipperFilePath(
                    EXAMPLE_FLIPPER_KEY.path.folder,
                    "key.nfc"
                ),
                content = EXAMPLE_FLIPPER_KEY.mainFile.content
            )
        )

        Assert.assertFalse(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `edit note of key`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            notes = "test"
        )

        Assert.assertFalse(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `edit note of key equals`() = runTest {
        val oldKey = EXAMPLE_FLIPPER_KEY.copy(
            notes = String("test".toByteArray())
        )
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            notes = "test"
        )

        Assert.assertFalse(
            ShouldSynchronizeHelper.isShouldSynchronize(
                oldKey,
                newKey
            )
        )
    }

    @Test
    fun `not edit content of file, just move`() = runTest {
        FlipperStorageProvider
            .useTemporaryFile(ApplicationProvider.getApplicationContext()) { tmpFile ->
                tmpFile.writeText("Hello, world")
                val newKey = EXAMPLE_FLIPPER_KEY.copy(
                    mainFile = FlipperFile(
                        path = FlipperFilePath(
                            folder = "test",
                            nameWithExtension = "key.nfc"
                        ),
                        content = FlipperKeyContent.InternalFile(tmpFile.absolutePath)
                    )
                )

                Assert.assertFalse(
                    ShouldSynchronizeHelper.isShouldSynchronize(
                        EXAMPLE_FLIPPER_KEY,
                        newKey
                    )
                )
            }
    }

    @Test
    fun `edit content of file, not edit name`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            mainFile = FlipperFile(
                path = FlipperFilePath(
                    folder = "test",
                    nameWithExtension = "key.nfc"
                ),
                content = FlipperKeyContent.RawData("Hello, world2".toByteArray())
            )
        )

        Assert.assertTrue(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `edit name and content`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            mainFile = FlipperFile(
                path = FlipperFilePath(
                    folder = "test",
                    nameWithExtension = "key2.nfc"
                ),
                content = FlipperKeyContent.RawData("Hello, world2".toByteArray())
            )
        )

        Assert.assertTrue(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `not edit additional file, create new file`() = runTest {
        val newAdditionalFiles = ArrayList(EXAMPLE_FLIPPER_KEY.additionalFiles)
        newAdditionalFiles.add(
            FlipperFile(
                path = FlipperFilePath(
                    folder = "test",
                    nameWithExtension = "additional_file2.nfc"
                ),
                content = FlipperKeyContent.RawData("Hello from additional file".toByteArray())
            )
        )
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            additionalFiles = newAdditionalFiles.toPersistentList()
        )
        Assert.assertTrue(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `not edit additional file, not create new file`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            additionalFiles = persistentListOf(
                FlipperFile(
                    path = FlipperFilePath(
                        folder = "test",
                        nameWithExtension = "additional_file.nfc"
                    ),
                    content = FlipperKeyContent.RawData("Hello from additional file".toByteArray())
                )
            )
        )
        Assert.assertFalse(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `edit additional file name`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            additionalFiles = persistentListOf(
                EXAMPLE_FLIPPER_KEY.additionalFiles.first().copy(
                    path = FlipperFilePath(
                        folder = "test",
                        nameWithExtension = "additional_file2.nfc"
                    )
                )
            )
        )

        Assert.assertTrue(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `edit additional file content`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            additionalFiles = persistentListOf(
                EXAMPLE_FLIPPER_KEY.additionalFiles.first().copy(
                    content = FlipperKeyContent.RawData("Hello from additional file2".toByteArray())
                )
            )
        )

        Assert.assertTrue(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `not edit additional file content, just move`() = runTest {
        FlipperStorageProvider
            .useTemporaryFile(ApplicationProvider.getApplicationContext()) { tmpFile ->
                tmpFile.writeText("Hello from additional file")
                val newKey = EXAMPLE_FLIPPER_KEY.copy(
                    additionalFiles = persistentListOf(
                        FlipperFile(
                            path = FlipperFilePath(
                                folder = "test",
                                nameWithExtension = "additional_file.nfc"
                            ),
                            content = FlipperKeyContent.InternalFile(tmpFile.absolutePath)
                        )
                    )
                )
                Assert.assertFalse(
                    ShouldSynchronizeHelper.isShouldSynchronize(
                        EXAMPLE_FLIPPER_KEY,
                        newKey
                    )
                )
            }
    }

    @Test
    fun `add additional file`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            additionalFiles = EXAMPLE_FLIPPER_KEY.additionalFiles.plus(
                FlipperFile(
                    path = FlipperFilePath(
                        folder = "test",
                        nameWithExtension = "additional_file.nfc"
                    ),
                    content = FlipperKeyContent.RawData("Hello from additional file".toByteArray())
                )
            ).toPersistentList()
        )

        Assert.assertTrue(
            ShouldSynchronizeHelper.isShouldSynchronize(
                EXAMPLE_FLIPPER_KEY,
                newKey
            )
        )
    }

    @Test
    fun `remove additional file - not empty`() = runTest {
        val oldKey = EXAMPLE_FLIPPER_KEY.copy(
            additionalFiles = EXAMPLE_FLIPPER_KEY.additionalFiles.plus(
                FlipperFile(
                    path = FlipperFilePath(
                        folder = "test",
                        nameWithExtension = "additional_file.nfc"
                    ),
                    content = FlipperKeyContent.RawData("Hello from additional file".toByteArray())
                )
            ).toPersistentList()
        )
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            additionalFiles = persistentListOf(
                FlipperFile(
                    path = FlipperFilePath(
                        folder = "test",
                        nameWithExtension = "additional_file.nfc"
                    ),
                    content = FlipperKeyContent.RawData("Hello from additional file".toByteArray())
                )
            )
        )
        Assert.assertTrue(ShouldSynchronizeHelper.isShouldSynchronize(oldKey, newKey))
    }

    @Test
    fun `remove additional file - empty`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            additionalFiles = persistentListOf()
        )

        Assert.assertTrue(ShouldSynchronizeHelper.isShouldSynchronize(EXAMPLE_FLIPPER_KEY, newKey))
    }

    @Test
    fun `mark deleted`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            deleted = true
        )

        Assert.assertTrue(ShouldSynchronizeHelper.isShouldSynchronize(EXAMPLE_FLIPPER_KEY, newKey))
    }

    @Test
    fun `mark deleted - ignore`() = runTest {
        val newKey = EXAMPLE_FLIPPER_KEY.copy(
            deleted = false
        )

        Assert.assertFalse(ShouldSynchronizeHelper.isShouldSynchronize(EXAMPLE_FLIPPER_KEY, newKey))
    }

    @Test
    fun `unmark deleted`() = runTest {
        val oldKey = EXAMPLE_FLIPPER_KEY.copy(
            deleted = true
        )
        val newKey = oldKey.copy(
            deleted = false
        )
        Assert.assertTrue(ShouldSynchronizeHelper.isShouldSynchronize(oldKey, newKey))
    }

    @Test
    fun `unmark deleted - ignore`() = runTest {
        val oldKey = EXAMPLE_FLIPPER_KEY.copy(
            deleted = true
        )
        val newKey = oldKey.copy(
            deleted = true
        )
        Assert.assertFalse(ShouldSynchronizeHelper.isShouldSynchronize(oldKey, newKey))
    }
}
