package com.flipperdevices.bridge.dao.impl.api.key

import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.bridge.dao.impl.comparator.FileComparatorExt.isSameContent
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.InputStream
import java.util.UUID

class FileComparatorTest {
    private lateinit var fileComparator: FileComparator

    @Before
    fun setUp() {
        fileComparator = DefaultFileComparator
    }

    /**
     * Create temp file filled with [content]
     */
    private fun createTempFile(content: String): File {
        val name = UUID.randomUUID().toString()
        val extensionWithDot = ".txt"
        return File.createTempFile(name, extensionWithDot).apply {
            writeText(content)
        }
    }

    /**
     * Create empty [InputStream] for non-existing files
     */
    private inline fun File.inputStreamOrEmpty(): InputStream {
        return if (exists()) {
            inputStream()
        } else {
            InputStream.nullInputStream()
        }
    }

    @Test
    fun GIVEN_not_existing_file_WHEN_is_equal_THEN_true() {
        runTest {
            val file1 = File(UUID.randomUUID().toString())
            val file2 = File(UUID.randomUUID().toString())
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
            val file1 = createTempFile("CONTENT")
            val file2 = File(UUID.randomUUID().toString())
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
            val file1 = createTempFile("CONTENT")
            val file2 = createTempFile("CONTENT")
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
            val file1 = createTempFile(UUID.randomUUID().toString())
            val file2 = createTempFile(UUID.randomUUID().toString())
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
