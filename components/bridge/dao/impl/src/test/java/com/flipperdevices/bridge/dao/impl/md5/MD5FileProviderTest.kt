package com.flipperdevices.bridge.dao.impl.md5

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID

class MD5FileProviderTest {
    private lateinit var context: Context

    private fun getTempFolder(): File {
        val tempFolder = File.createTempFile(UUID.randomUUID().toString(), ".txt")
            .parentFile
            ?: error("Could not get temp folder")
        return File(tempFolder, "${UUID.randomUUID()}").also { it.mkdirs() }
    }

    @Before
    fun setUp() {
        context = mockk()

        every {
            context.filesDir
        } returns getTempFolder()
    }

    private fun createMD5FileProvider(): MD5FileProviderImpl {
        return MD5FileProviderImpl(
            context = context,
            keyFolder = getTempFolder(),
            fileComparator = DefaultFileComparator
        )
    }

    @Test
    fun GIVEN_two_same_md5_same_content_WHEN_get_path_THEN_same_paths() {
        runTest {
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = "MD5",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            ).also { file ->
                file.createNewFile()
                file.writeBytes(byteArrayOf(0))
            }.path
            val file2 = md5FileProvider.getPathToFile(
                contentMd5 = "MD5",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            ).path
            Assert.assertEquals(file1, file2)
        }
    }

    @Test
    fun GIVEN_two_same_md5_different_content_WHEN_get_path_THEN_different_paths() {
        runTest {
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = "MD5",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            ).also { file ->
                file.createNewFile()
                file.writeBytes(byteArrayOf(0))
            }.path
            val file2 = md5FileProvider.getPathToFile(
                contentMd5 = "MD5",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(1))
            ).path
            Assert.assertNotEquals(file1, file2)
        }
    }

    @Test
    fun GIVEN_two_different_md5_different_content_WHEN_get_path_THEN_different_paths() {
        runTest {
            val md5FileProvider = createMD5FileProvider()
            val file1 = md5FileProvider.getPathToFile(
                contentMd5 = "MD51",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(0))
            ).also { file ->
                file.createNewFile()
                file.writeBytes(byteArrayOf(0))
            }.path
            val file2 = md5FileProvider.getPathToFile(
                contentMd5 = "MD52",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(1))
            ).path
            Assert.assertNotEquals(file1, file2)
        }
    }
}
