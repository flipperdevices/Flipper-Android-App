package com.flipperdevices.bridge.dao.impl.md5

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.FileExt
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MD5FileProviderTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mockk()
        every { context.filesDir } returns FileExt.tempDir
    }

    private fun createMD5FileProvider(): MD5FileProviderImpl {
        return MD5FileProviderImpl(
            context = context,
            keyFolder = FileExt.tempDir,
            fileComparator = DefaultFileComparator
        )
    }

    @Test
    fun GIVEN_two_same_md5_same_content_WHEN_get_path_THEN_same_paths() {
        runTest {
            val emptyKeyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = FileExt.STUB_MD5,
                keyContent = emptyKeyContent
            ).also { file ->
                file.createNewFile()
                file.writeBytes(emptyKeyContent.bytes)
            }.path
            val file2 = md5FileProvider.getPathToFile(
                contentMd5 = FileExt.STUB_MD5,
                keyContent = emptyKeyContent
            ).path
            Assert.assertEquals(file1, file2)
        }
    }

    @Test
    fun GIVEN_two_same_md5_different_content_WHEN_get_path_THEN_different_paths() {
        runTest {
            val emptyKeyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = FileExt.STUB_MD5,
                keyContent = emptyKeyContent
            ).also { file ->
                file.createNewFile()
                file.writeBytes(emptyKeyContent.bytes)
            }.path
            val file2 = md5FileProvider.getPathToFile(
                contentMd5 = FileExt.STUB_MD5,
                keyContent = FlipperKeyContent.RawData(byteArrayOf(1))
            ).path
            Assert.assertNotEquals(file1, file2)
        }
    }

    @Test
    fun GIVEN_two_different_md5_different_content_WHEN_get_path_THEN_different_paths() {
        runTest {
            val emptyKeyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = FileExt.RANDOM_MD5,
                keyContent = emptyKeyContent
            ).also { file ->
                file.createNewFile()
                file.writeBytes(emptyKeyContent.bytes)
            }.path
            val file2 = md5FileProvider.getPathToFile(
                contentMd5 = FileExt.RANDOM_MD5,
                keyContent = FlipperKeyContent.RawData(byteArrayOf(1))
            ).path
            Assert.assertNotEquals(file1, file2)
        }
    }
}
