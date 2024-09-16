package com.flipperdevices.bridge.dao.impl.api.key

import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.bridge.dao.impl.converters.DatabaseKeyContentConverter
import com.flipperdevices.bridge.dao.impl.converters.StubMD5Converter
import com.flipperdevices.bridge.dao.impl.md5.MD5FileProviderImpl
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.test.FlipperStorageProviderTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class DatabaseKeyContentConverterTest {

    private lateinit var context: Context

    @get:Rule
    val storageProviderRule = FlipperStorageProviderTestRule()
    private lateinit var storageProvider: FlipperStorageProvider

    @Before
    fun setUp() {
        context = mockk()

        storageProvider = storageProviderRule.flipperStorageProvider

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
                fileComparator = FileComparator(storageProvider),
                storageProvider = storageProvider
            ),
            flipperStorageProvider = storageProvider
        )
    }

    private fun Path.toKeyContentPath(converter: DatabaseKeyContentConverter): String? {
        val fkContent = FlipperKeyContent.RawData(
            if (storageProvider.fileSystem.exists(this)) {
                storageProvider.fileSystem.source(this).buffer().use { it.readByteArray() }
            } else {
                byteArrayOf()
            }
        )
        val dkContent = DatabaseKeyContent(fkContent)
        return converter.keyContentToPath(dkContent)
    }

    @Test
    fun GIVEN_different_content_WHEN_different_md_5_THEN_different_path() {
        runTest {
            val path1 = "FILE_1".toPath().apply {
                storageProvider.fileSystem.write(this) { writeUtf8("FILLED_CONTENT_1") }
            }.toKeyContentPath(createDatabaseKeyConverter())

            val path2 = "FILE_2".toPath().apply {
                storageProvider.fileSystem.write(this) { writeUtf8("FILLED_CONTENT_2") }
            }.toKeyContentPath(createDatabaseKeyConverter())

            Assert.assertNotEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_different_content_WHEN_same_md_5_THEN_different_path() {
        runTest {
            val databaseKeyContentConverter = createDatabaseKeyConverter()
            val path1 = "FILE_1".toPath().apply {
                storageProvider.fileSystem.write(this) { writeUtf8("FILLED_CONTENT_1") }
            }.toKeyContentPath(databaseKeyContentConverter)

            val path2 = "FILE_2".toPath().apply {
                storageProvider.fileSystem.write(this) { writeUtf8("FILLED_CONTENT_2") }
            }.toKeyContentPath(databaseKeyContentConverter)

            Assert.assertNotEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_same_content_WHEN_same_md_5_THEN_same_path() {
        runTest {
            val databaseKeyContentConverter = createDatabaseKeyConverter()
            val path1 = "FILE_1".toPath().apply {
                storageProvider.fileSystem.write(this) {
                    writeUtf8("SAME_TEXT")
                }
            }.toKeyContentPath(databaseKeyContentConverter)

            val path2 = "FILE_2".toPath().apply {
                storageProvider.fileSystem.write(this) {
                    writeUtf8("SAME_TEXT")
                }
            }.toKeyContentPath(databaseKeyContentConverter)

            Assert.assertEquals(path1, path2)
        }
    }

    @Test
    fun GIVEN_not_existing_files_WHEN_same_md_5_THEN_same_path() {
        runTest {
            val databaseKeyContentConverter = createDatabaseKeyConverter()
            val path1 = "FILE_CONTENT_1".toPath()
                .toKeyContentPath(databaseKeyContentConverter)

            val path2 = "FILE_CONTENT_2".toPath()
                .toKeyContentPath(databaseKeyContentConverter)

            Assert.assertEquals(path1, path2)
        }
    }
}
