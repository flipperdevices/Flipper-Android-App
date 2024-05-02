package com.flipperdevices.bridge.dao.impl.api.key

import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import com.flipperdevices.bridge.dao.impl.converters.DatabaseKeyContentConverter
import com.flipperdevices.bridge.dao.impl.converters.LambdaMD5Converter
import com.flipperdevices.bridge.dao.impl.md5.MD5FileProviderImpl
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.thread.StubMainThreadChecker
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class DatabaseKeyContentConverterTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mockk()
        every {
            context.filesDir
        } returns File.createTempFile(UUID.randomUUID().toString(), ".txt").parentFile
    }

    private fun getRandomFolder(): File {
        val parentFolder = File.createTempFile(UUID.randomUUID().toString(), ".txt").parentFile
        val folderName = UUID.randomUUID().toString()
        val childFolder = File(parentFolder, "./$folderName")
        if (childFolder.exists()) childFolder.delete()
        childFolder.mkdirs()
        return childFolder
    }

    private fun createDatabaseKeyConverter(md5: String): DatabaseKeyContentConverter {
        val md5Converter = LambdaMD5Converter { md5 }
        return DatabaseKeyContentConverter(
            md5Converter = md5Converter,
            mainThreadChecker = StubMainThreadChecker,
            mD5FileProvider = MD5FileProviderImpl(
                context = context,
                fileComparator = DefaultFileComparator,
                keyFolder = getRandomFolder(),
                md5Converter = md5Converter
            )
        )
    }

    /**
     * Create temp file filled with [content]
     */
    private fun createFilledFile(content: String): File {
        val parentFolder = getRandomFolder()
        val fileName = UUID.randomUUID().toString()
        return File(parentFolder, fileName).apply {
            writeText(content)
        }
    }

    @Test
    fun GIVEN_different_content_WHEN_different_md_5_THEN_different_path() {
        runTest {
            getRandomFolder().delete()
            val path1 = createFilledFile("CONTENT_1").path
                .let(FlipperKeyContent::InternalFile)
                .let(::DatabaseKeyContent)
                .let { createDatabaseKeyConverter("ONE_MD").keyContentToPath(it) }

            val path2 = createFilledFile("CONTENT_2").path
                .let(FlipperKeyContent::InternalFile)
                .let(::DatabaseKeyContent)
                .let { createDatabaseKeyConverter("TWO_MD").keyContentToPath(it) }

            Assert.assertNotEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_different_content_WHEN_same_md_5_THEN_different_path() {
        runTest {
            getRandomFolder().delete()
            val databaseKeyContentConverter = createDatabaseKeyConverter("SAME_MD")
            val path1 = createFilledFile("CONTENT_1").path
                .let(FlipperKeyContent::InternalFile)
                .let(::DatabaseKeyContent)
                .let { databaseKeyContentConverter.keyContentToPath(it) }

            val path2 = createFilledFile("CONTENT_2").path
                .let(FlipperKeyContent::InternalFile)
                .let(::DatabaseKeyContent)
                .let { databaseKeyContentConverter.keyContentToPath(it) }

            Assert.assertNotEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_same_content_WHEN_same_md_5_THEN_same_path() {
        runTest {
            getRandomFolder().delete()
            val databaseKeyContentConverter = createDatabaseKeyConverter("SAME_MD")
            val path1 = createFilledFile("CONTENT_1").path
                .let(FlipperKeyContent::InternalFile)
                .let(::DatabaseKeyContent)
                .let { databaseKeyContentConverter.keyContentToPath(it) }

            val path2 = createFilledFile("CONTENT_1").path
                .let(FlipperKeyContent::InternalFile)
                .let(::DatabaseKeyContent)
                .let { databaseKeyContentConverter.keyContentToPath(it) }

            Assert.assertEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_not_existing_files_WHEN_same_md_5_THEN_same_path() {
        runTest {
            val databaseKeyContentConverter = createDatabaseKeyConverter("SAME_MD")
            val path1 = File("FILE_1").path
                .let(FlipperKeyContent::InternalFile)
                .let(::DatabaseKeyContent)
                .let { databaseKeyContentConverter.keyContentToPath(it) }

            val path2 = File("FILE_2").path
                .let(FlipperKeyContent::InternalFile)
                .let(::DatabaseKeyContent)
                .let { databaseKeyContentConverter.keyContentToPath(it) }

            Assert.assertEquals(path1, path2)
        }
    }
}
