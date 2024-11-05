package com.flipperdevices.bridge.dao.impl.md5

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.test.FlipperStorageProviderTestRule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MD5FileProviderTest {
    private lateinit var context: Context

    @get:Rule
    val storageProviderRule = FlipperStorageProviderTestRule()
    private lateinit var storageProvider: FlipperStorageProvider

    @Before
    fun setUp() {
        context = mockk()
        storageProvider = storageProviderRule.flipperStorageProvider
    }

    private fun createMD5FileProvider(): MD5FileProviderImpl {
        return MD5FileProviderImpl(
            fileComparator = FileComparator(storageProvider),
            storageProvider = storageProvider
        )
    }

    @Test
    fun GIVEN_two_same_md5_same_content_WHEN_get_path_THEN_same_paths() {
        runTest {
            val emptyKeyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = "STUB_MD5",
                keyContent = emptyKeyContent
            )
            storageProvider.mkdirsParent(file1)
            storageProvider.fileSystem.write(file1) { write(emptyKeyContent.bytes) }

            val actualPath = md5FileProvider.getPathToFile(
                contentMd5 = "STUB_MD5",
                keyContent = emptyKeyContent
            )
            Assert.assertEquals(file1, actualPath)
        }
    }

    @Test
    fun GIVEN_two_same_md5_different_content_WHEN_get_path_THEN_different_paths() {
        runTest {
            val emptyKeyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = "STUB_MD5",
                keyContent = emptyKeyContent
            )
            storageProvider.mkdirsParent(file1)
            storageProvider.fileSystem.write(file1) { write(emptyKeyContent.bytes) }

            val actual = md5FileProvider.getPathToFile(
                contentMd5 = "STUB_MD5",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(1))
            )
            Assert.assertNotEquals(file1, actual)
        }
    }

    @Test
    fun GIVEN_two_different_md5_different_content_WHEN_get_path_THEN_different_paths() {
        runTest {
            val emptyKeyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = "FIRST_MD5",
                keyContent = emptyKeyContent
            )
            storageProvider.mkdirsParent(file1)
            storageProvider.fileSystem.write(file1) { write(emptyKeyContent.bytes) }

            val actual = md5FileProvider.getPathToFile(
                contentMd5 = "SECOND_MD5",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(1))
            )
            Assert.assertNotEquals(file1, actual)
        }
    }
}
