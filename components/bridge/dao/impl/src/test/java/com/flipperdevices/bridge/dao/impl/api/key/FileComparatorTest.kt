package com.flipperdevices.bridge.dao.impl.api.key

import com.flipperdevices.bridge.dao.impl.FileExt
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.bridge.dao.impl.comparator.FileComparatorExt.isSameContent
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.InputStream

class FileComparatorTest {
    private lateinit var fileComparator: FileComparator

    @Before
    fun setUp() {
        fileComparator = DefaultFileComparator
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
            val file1 = File(FileExt.RANDOM_FILE_NAME)
            val file2 = File(FileExt.RANDOM_FILE_NAME)
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
            val file1 = FileExt.createFilledFile(FileExt.RANDOM_CONTENT)
            val file2 = File(FileExt.RANDOM_FILE_NAME)
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
            val file1 = FileExt.createFilledFile(FileExt.STUB_CONTENT)
            val file2 = FileExt.createFilledFile(FileExt.STUB_CONTENT)
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
            val file1 = FileExt.createFilledFile(FileExt.RANDOM_FILE_NAME)
            val file2 = FileExt.createFilledFile(FileExt.RANDOM_FILE_NAME)
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
