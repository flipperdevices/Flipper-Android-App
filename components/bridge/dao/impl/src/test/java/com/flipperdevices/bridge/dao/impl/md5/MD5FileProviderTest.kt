package com.flipperdevices.bridge.dao.impl.md5

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import com.flipperdevices.core.ktx.jre.createNewFileWithMkDirs
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class MD5FileProviderTest {
    private lateinit var context: Context

    @get:Rule
    val folder: TemporaryFolder = TemporaryFolder()
    private lateinit var tempFolder: File

    @Before
    fun setUp() {
        context = mockk()
        tempFolder = folder.newFolder()
        every { context.filesDir } returns tempFolder
    }

    @After
    fun onStop() {
        tempFolder.delete()
    }

    private fun createMD5FileProvider(): MD5FileProviderImpl {
        return MD5FileProviderImpl(
            context = context,
            fileComparator = DefaultFileComparator()
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
            file1.createNewFileWithMkDirs()
            file1.writeBytes(emptyKeyContent.bytes)
            val path1 = file1.path

            val path2 = md5FileProvider.getPathToFile(
                contentMd5 = "STUB_MD5",
                keyContent = emptyKeyContent
            ).path
            Assert.assertEquals(path1, path2)
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
            file1.createNewFileWithMkDirs()
            file1.writeBytes(emptyKeyContent.bytes)
            val path1 = file1.path

            val path2 = md5FileProvider.getPathToFile(
                contentMd5 = "STUB_MD5",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(1))
            ).path
            Assert.assertNotEquals(path1, path2)
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
            file1.createNewFileWithMkDirs()
            file1.writeBytes(emptyKeyContent.bytes)
            val path1 = file1.path

            val path2 = md5FileProvider.getPathToFile(
                contentMd5 = "SECOND_MD5",
                keyContent = FlipperKeyContent.RawData(byteArrayOf(1))
            ).path
            Assert.assertNotEquals(path1, path2)
        }
    }
}
