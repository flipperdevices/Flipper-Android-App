package com.flipperdevices.bridge.dao.impl.api.key

import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.FileExt
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import com.flipperdevices.bridge.dao.impl.converters.DatabaseKeyContentConverter
import com.flipperdevices.bridge.dao.impl.converters.StubMD5Converter
import com.flipperdevices.bridge.dao.impl.md5.MD5FileProviderImpl
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class DatabaseKeyContentConverterTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mockk()
        every { context.filesDir } returns FileExt.tempDir

        mockkStatic(Looper::class)
        every { Looper.myLooper() } returns mock<Looper>()
        every { Looper.getMainLooper() } returns mock<Looper>()

        mockkStatic(ShadowLooper::class)
        every { Shadows.shadowOf(Looper.myLooper()) } returns mock<ShadowLooper>()
        every { Shadows.shadowOf(Looper.getMainLooper()) } returns mock<ShadowLooper>()
    }

    private fun createDatabaseKeyConverter(): DatabaseKeyContentConverter {
        return DatabaseKeyContentConverter(
            md5Converter = StubMD5Converter,
            mD5FileProvider = MD5FileProviderImpl(
                context = context,
                fileComparator = DefaultFileComparator,
                keyFolder = FileExt.getRandomFolder(),
            )
        )
    }

    private fun File.toKeyContentPath(converter: DatabaseKeyContentConverter): String? {
        val fkContent = FlipperKeyContent.InternalFile(path)
        val dkContent = DatabaseKeyContent(fkContent)
        return converter.keyContentToPath(dkContent)
    }

    @Test
    fun GIVEN_different_content_WHEN_different_md_5_THEN_different_path() {
        runTest {
            val path1 = FileExt.createFilledFile(FileExt.RANDOM_FILE_NAME)
                .toKeyContentPath(createDatabaseKeyConverter())

            val path2 = FileExt.createFilledFile(FileExt.RANDOM_FILE_NAME)
                .toKeyContentPath(createDatabaseKeyConverter())

            Assert.assertNotEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_different_content_WHEN_same_md_5_THEN_different_path() {
        runTest {
            val databaseKeyContentConverter = createDatabaseKeyConverter()
            val path1 = FileExt.createFilledFile(FileExt.RANDOM_FILE_NAME)
                .toKeyContentPath(databaseKeyContentConverter)

            val path2 = FileExt.createFilledFile(FileExt.RANDOM_FILE_NAME)
                .toKeyContentPath(databaseKeyContentConverter)

            Assert.assertNotEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_same_content_WHEN_same_md_5_THEN_same_path() {
        runTest {
            val databaseKeyContentConverter = createDatabaseKeyConverter()
            val path1 = FileExt.createFilledFile(FileExt.DEFAULT_TEXT)
                .toKeyContentPath(databaseKeyContentConverter)

            val path2 = FileExt.createFilledFile(FileExt.DEFAULT_TEXT)
                .toKeyContentPath(databaseKeyContentConverter)

            Assert.assertEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_not_existing_files_WHEN_same_md_5_THEN_same_path() {
        runTest {
            val databaseKeyContentConverter = createDatabaseKeyConverter()
            val path1 = File(FileExt.RANDOM_FILE_NAME)
                .toKeyContentPath(databaseKeyContentConverter)

            val path2 = File(FileExt.RANDOM_FILE_NAME)
                .toKeyContentPath(databaseKeyContentConverter)

            Assert.assertEquals(path1, path2)
        }
    }
}
