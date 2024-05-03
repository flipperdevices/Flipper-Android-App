package com.flipperdevices.bridge.dao.impl.api.key

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.bridge.dao.impl.comparator.FileComparatorExt.isSameContent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class FileComparatorTest {
    private lateinit var fileComparator: FileComparator

    @get:Rule
    val folder: TemporaryFolder = TemporaryFolder()
    private lateinit var tempFolder: File

    @Before
    fun setUp() {
        tempFolder = folder.newFolder()
        fileComparator = DefaultFileComparator()
    }

    @After
    fun onStop() {
        tempFolder.delete()
    }

    /**
     * Create empty [InputStream] for non-existing files
     */
    private fun File.inputStreamOrEmpty(): InputStream {
        return if (exists()) {
            inputStream()
        } else {
            InputStream.nullInputStream()
        }
    }

    @Test
    fun GIVEN_not_existing_file_WHEN_is_equal_THEN_true() {
        runTest {
            val file1 = File("FILE_NAME_1")
            val file2 = File("FILE_NAME_2")
            fileComparator.isSameContent(
                file1.inputStreamOrEmpty(),
                file2.inputStreamOrEmpty(),
            ).run(Assert::assertTrue)
            fileComparator.isSameContent(
                file1,
                file2,
            ).run(Assert::assertTrue)
        }
    }

    @Test
    fun GIVEN_not_existing_and_existing_file_WHEN_is_equal_THEN_false() {
        runTest {
            val file1 = File(tempFolder, "FILE_1").apply {
                writeText("RANDOM_CONTENT")
            }
            val file2 = File("ANOTHER_RANDOM_NAME")
            fileComparator.isSameContent(
                file1.inputStreamOrEmpty(),
                file2.inputStreamOrEmpty(),
            ).run(Assert::assertFalse)
            fileComparator.isSameContent(
                file1,
                file2,
            ).run(Assert::assertFalse)
        }
    }

    @Test
    fun GIVEN_two_same_content_files_WHEN_is_equal_THEN_true() {
        runTest {
            val file1 = File(tempFolder, "FILE_1").apply {
                writeText("STUB_CONTENT")
            }
            val file2 = File(tempFolder, "FILE_2").apply {
                writeText("STUB_CONTENT")
            }
            fileComparator.isSameContent(
                file1.inputStreamOrEmpty(),
                file2.inputStreamOrEmpty(),
            ).run(Assert::assertTrue)
            fileComparator.isSameContent(
                file1,
                file2,
            ).run(Assert::assertTrue)
        }
    }

    @Test
    fun GIVEN_two_different_content_files_WHEN_is_equal_THEN_false() {
        runTest {
            val file1 = File(tempFolder, "FILE_1").apply {
                writeText("FILLED_CONTENT_1")
            }
            val file2 = File(tempFolder, "FILE_2").apply {
                writeText("FILLED_CONTENT_2")
            }
            fileComparator.isSameContent(
                file1.inputStreamOrEmpty(),
                file2.inputStreamOrEmpty(),
            ).run(Assert::assertFalse)
            fileComparator.isSameContent(
                file1,
                file2
            ).run(Assert::assertFalse)
        }
    }
}
