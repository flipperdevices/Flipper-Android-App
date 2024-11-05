package com.flipperdevices.bridge.dao.impl.api.key

import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.sourceOrEmpty
import com.flipperdevices.core.test.FlipperStorageProviderTestRule
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FileComparatorTest {
    private lateinit var fileComparator: FileComparator

    @get:Rule
    val storageProviderRule = FlipperStorageProviderTestRule()
    private lateinit var storageProvider: FlipperStorageProvider

    @Before
    fun setUp() {
        storageProvider = storageProviderRule.flipperStorageProvider
        fileComparator = FileComparator(storageProvider)
    }

    @Test
    fun GIVEN_not_existing_file_WHEN_is_equal_THEN_true() {
        runTest {
            val file1 = "FILE_NAME_1".toPath()
            val file2 = "FILE_NAME_2".toPath()
            storageProvider.fileSystem.sourceOrEmpty(file1).buffer().use { source1 ->
                storageProvider.fileSystem.sourceOrEmpty(file2).buffer().use { source2 ->
                    fileComparator.isSameContent(
                        source1,
                        source2
                    ).run(Assert::assertTrue)
                }
            }
            fileComparator.isSameContent(
                file1,
                file2,
            ).run(Assert::assertTrue)
        }
    }

    @Test
    fun GIVEN_not_existing_and_existing_file_WHEN_is_equal_THEN_false() {
        runTest {
            val file1 = "FILE_1".toPath()
            storageProvider.fileSystem.write(file1) {
                writeUtf8("RANDOM_CONTENT")
            }
            val file2 = "ANOTHER_RANDOM_NAME".toPath()

            storageProvider.fileSystem.sourceOrEmpty(file1).buffer().use { source1 ->
                storageProvider.fileSystem.sourceOrEmpty(file2).buffer().use { source2 ->
                    fileComparator.isSameContent(
                        source1,
                        source2
                    ).run(Assert::assertFalse)
                }
            }
            fileComparator.isSameContent(
                file1,
                file2,
            ).run(Assert::assertFalse)
        }
    }

    @Test
    fun GIVEN_two_same_content_files_WHEN_is_equal_THEN_true() {
        runTest {
            val file1 = "FILE_1".toPath()
            storageProvider.fileSystem.write(file1) {
                writeUtf8("STUB_CONTENT")
            }
            val file2 = "FILE_2".toPath()
            storageProvider.fileSystem.write(file2) {
                writeUtf8("STUB_CONTENT")
            }
            storageProvider.fileSystem.sourceOrEmpty(file1).buffer().use { source1 ->
                storageProvider.fileSystem.sourceOrEmpty(file2).buffer().use { source2 ->
                    fileComparator.isSameContent(
                        source1,
                        source2
                    ).run(Assert::assertTrue)
                }
            }
            fileComparator.isSameContent(
                file1,
                file2,
            ).run(Assert::assertTrue)
        }
    }

    @Test
    fun GIVEN_two_different_content_files_WHEN_is_equal_THEN_false() {
        runTest {
            val file1 = "FILE_1".toPath()
            storageProvider.fileSystem.write(file1) {
                writeUtf8("FILLED_CONTENT_1")
            }
            val file2 = "FILE_2".toPath()
            storageProvider.fileSystem.write(file2) {
                writeUtf8("FILLED_CONTENT_2")
            }
            storageProvider.fileSystem.sourceOrEmpty(file1).buffer().use { source1 ->
                storageProvider.fileSystem.sourceOrEmpty(file2).buffer().use { source2 ->
                    fileComparator.isSameContent(
                        source1,
                        source2
                    ).run(Assert::assertFalse)
                }
            }
            fileComparator.isSameContent(
                file1,
                file2
            ).run(Assert::assertFalse)
        }
    }

    @Test
    fun GIVEN_two_different_size_files_WHEN_is_equal_THEN_false() {
        runTest {
            val file1 = "FILE_1".toPath()
            storageProvider.fileSystem.write(file1) {
                writeUtf8("FILLED_CONTENT_1")
            }
            val file2 = "FILE_2".toPath()
            storageProvider.fileSystem.write(file2) {
                writeUtf8("FILLED_CONTENT")
            }
            storageProvider.fileSystem.sourceOrEmpty(file1).buffer().use { source1 ->
                storageProvider.fileSystem.sourceOrEmpty(file2).buffer().use { source2 ->
                    fileComparator.isSameContent(
                        source1,
                        source2
                    ).run(Assert::assertFalse)
                }
            }
            storageProvider.fileSystem.sourceOrEmpty(file1).buffer().use { source1 ->
                storageProvider.fileSystem.sourceOrEmpty(file2).buffer().use { source2 ->
                    fileComparator.isSameContent(
                        source2,
                        source1
                    ).run(Assert::assertFalse)
                }
            }
            fileComparator.isSameContent(
                file1,
                file2
            ).run(Assert::assertFalse)
        }
    }
}
