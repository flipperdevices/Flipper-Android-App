package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.repository.android.AndroidHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.TimestampSynchronizationChecker
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestTimestampRepository
import com.flipperdevices.bridge.synchronization.impl.utils.detailedProgressWrapperTrackerStub
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

private const val TEST_LAST_TIMESTAMP = 1000L
private const val TYPE_NEED_INVALIDATE_TIMESTAMP = 2000L
private val TEST_FLIPPER_KEY = FlipperKey(
    mainFile = FlipperFile(
        FlipperFilePath("nfc", "test.nfc"),
        FlipperKeyContent.RawData(byteArrayOf())
    ),
    synchronized = false,
    deleted = false
)

class KeysSynchronizationTest {
    private lateinit var folderKeySynchronization: FolderKeySynchronization
    private lateinit var simpleKeyApi: SimpleKeyApi
    private lateinit var timestampSynchronizationChecker: TimestampSynchronizationChecker
    private lateinit var manifestTimestampRepository: ManifestTimestampRepository
    private lateinit var manifestRepository: ManifestRepository
    private lateinit var androidHashRepository: AndroidHashRepository
    private lateinit var underTest: KeysSynchronization

    @Before
    fun setUp() {
        folderKeySynchronization = mockk(relaxed = true)
        timestampSynchronizationChecker = mockk()
        manifestTimestampRepository = mockk(relaxUnitFun = true)
        manifestRepository = mockk()
        androidHashRepository = mockk()
        simpleKeyApi = mockk()

        underTest = KeysSynchronizationImpl(
            folderKeySynchronization = folderKeySynchronization,
            timestampSynchronizationChecker = timestampSynchronizationChecker,
            manifestTimestampRepository = manifestTimestampRepository,
            manifestRepository = manifestRepository,
            androidHashRepository = androidHashRepository,
            simpleKeyApi = simpleKeyApi
        )
    }

    @Test
    fun `skip synchronization`() = runTest {
        coEvery {
            timestampSynchronizationChecker.fetchFoldersTimestamp(
                any(),
                any()
            )
        } returns mapOf(
            FlipperKeyType.NFC to 0L
        )
        coEvery { simpleKeyApi.getExistKeys(eq(FlipperKeyType.NFC)) } returns emptyList()
        coEvery {
            androidHashRepository.getHashes(eq(emptyList()))
        } returns emptyList()
        coEvery {
            manifestRepository.compareFolderKeysWithManifest(
                eq(FlipperKeyType.NFC.flipperDir),
                eq(emptyList()),
                eq(DiffSource.ANDROID)
            )
        } returns emptyList()
        coEvery {
            manifestTimestampRepository.isUpdateRequired(
                eq(FlipperKeyType.NFC),
                eq(0L)
            )
        } returns false

        underTest.syncKeys(detailedProgressWrapperTrackerStub())

        coVerify(exactly = 0) { folderKeySynchronization.syncFolder(any(), any()) }
    }

    @Test
    fun `not skip synchronization if android have changes`() = runTest {
        coEvery {
            timestampSynchronizationChecker.fetchFoldersTimestamp(
                any(),
                any()
            )
        } returns mapOf(
            FlipperKeyType.NFC to TYPE_NEED_INVALIDATE_TIMESTAMP
        )
        coEvery { simpleKeyApi.getExistKeys(eq(FlipperKeyType.NFC)) } returns listOf(
            TEST_FLIPPER_KEY
        )
        coEvery { androidHashRepository.getHashes(eq(listOf(TEST_FLIPPER_KEY))) } returns listOf(
            KeyWithHash(TEST_FLIPPER_KEY.path, "HASH")
        )
        coEvery {
            manifestRepository.compareFolderKeysWithManifest(
                eq(FlipperKeyType.NFC.flipperDir),
                eq(listOf(KeyWithHash(TEST_FLIPPER_KEY.path, "HASH"))),
                eq(DiffSource.ANDROID)
            )
        } returns listOf(
            KeyDiff(
                KeyWithHash(TEST_FLIPPER_KEY.path, "HASH"),
                KeyAction.ADD,
                DiffSource.ANDROID
            )
        )

        underTest.syncKeys(detailedProgressWrapperTrackerStub())

        coVerify { folderKeySynchronization.syncFolder(eq(FlipperKeyType.NFC), any()) }
    }

    @Test
    fun `not skip synchronization if folder update is null`() = runTest {
        coEvery {
            timestampSynchronizationChecker.fetchFoldersTimestamp(
                any(),
                any()
            )
        } returns mapOf(
            FlipperKeyType.NFC to null
        )
        coEvery { simpleKeyApi.getExistKeys(eq(FlipperKeyType.NFC)) } returns emptyList()
        coEvery {
            androidHashRepository.getHashes(eq(emptyList()))
        } returns emptyList()
        coEvery {
            manifestRepository.compareFolderKeysWithManifest(
                eq(FlipperKeyType.NFC.flipperDir),
                eq(emptyList()),
                eq(DiffSource.ANDROID)
            )
        } returns emptyList()

        underTest.syncKeys(detailedProgressWrapperTrackerStub())

        coVerify(exactly = 0) { manifestTimestampRepository.isUpdateRequired(any(), any()) }
        coVerify { folderKeySynchronization.syncFolder(any(), any()) }
    }

    @Test
    fun `not skip synchronization if folder update is outdated`() = runTest {
        coEvery {
            timestampSynchronizationChecker.fetchFoldersTimestamp(
                any(),
                any()
            )
        } returns mapOf(
            FlipperKeyType.NFC to TEST_LAST_TIMESTAMP
        )
        coEvery { simpleKeyApi.getExistKeys(eq(FlipperKeyType.NFC)) } returns emptyList()
        coEvery {
            androidHashRepository.getHashes(eq(emptyList()))
        } returns emptyList()
        coEvery {
            manifestRepository.compareFolderKeysWithManifest(
                eq(FlipperKeyType.NFC.flipperDir),
                eq(emptyList()),
                eq(DiffSource.ANDROID)
            )
        } returns emptyList()
        coEvery {
            manifestTimestampRepository.isUpdateRequired(
                eq(FlipperKeyType.NFC),
                eq(TEST_LAST_TIMESTAMP)
            )
        } returns true

        underTest.syncKeys(detailedProgressWrapperTrackerStub())

        coVerify { folderKeySynchronization.syncFolder(any(), any()) }
    }
}
