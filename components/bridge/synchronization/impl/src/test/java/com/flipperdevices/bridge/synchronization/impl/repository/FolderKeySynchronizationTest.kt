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
import com.flipperdevices.bridge.synchronization.impl.repository.android.SynchronizationStateRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.detailedProgressWrapperTrackerStub
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FolderKeySynchronizationTest {
    private lateinit var androidHashRepository: AndroidHashRepository
    private lateinit var flipperHashRepository: FlipperHashRepository
    private lateinit var manifestRepository: ManifestRepository
    private lateinit var synchronizationRepository: SynchronizationStateRepository
    private lateinit var simpleKeyApi: SimpleKeyApi
    private lateinit var keyDiffApplier: KeyDiffApplier
    private lateinit var underTest: FolderKeySynchronization

    @Before
    fun setUp() {
        androidHashRepository = mockk()
        flipperHashRepository = mockk()
        manifestRepository = mockk(relaxUnitFun = true)
        synchronizationRepository = mockk(relaxUnitFun = true)
        simpleKeyApi = mockk()
        keyDiffApplier = mockk(relaxUnitFun = true)
        underTest = FolderKeySynchronizationImpl(
            androidHashRepository,
            flipperHashRepository,
            manifestRepository,
            synchronizationRepository,
            simpleKeyApi,
            keyDiffApplier
        )
    }

    @Test
    @Suppress("LongMethod")
    fun `correct application test`() = runTest {
        val keysFromAndroid = listOf(
            FlipperKey(
                mainFile = FlipperFile(
                    FlipperFilePath("nfc", "from_android.nfc"),
                    FlipperKeyContent.RawData(byteArrayOf())
                ),
                synchronized = false,
                deleted = false
            )
        )
        coEvery {
            simpleKeyApi.getExistKeys(eq(FlipperKeyType.NFC))
        } returns keysFromAndroid

        coEvery {
            androidHashRepository.getHashes(eq(keysFromAndroid))
        } returns listOf(
            KeyWithHash(FlipperFilePath("nfc", "from_android.nfc"), "ANDROIDHASH")
        )

        coEvery {
            flipperHashRepository.getHashesForType(
                eq(FlipperKeyType.NFC),
                any()
            )
        } returns listOf(
            KeyWithHash(FlipperFilePath("nfc", "from_flipper.nfc"), "FLIPPERHASH")
        )

        coEvery {
            manifestRepository.compareFolderKeysWithManifest(
                folder = eq("nfc"),
                keys = eq(
                    listOf(
                        KeyWithHash(FlipperFilePath("nfc", "from_android.nfc"), "ANDROIDHASH")
                    )
                ),
                diffSource = eq(DiffSource.ANDROID)
            )
        } returns listOf(
            KeyDiff(
                KeyWithHash(FlipperFilePath("nfc", "from_android.nfc"), "ANDROIDHASH"),
                KeyAction.ADD,
                DiffSource.ANDROID
            )
        )

        coEvery {
            manifestRepository.compareFolderKeysWithManifest(
                folder = eq("nfc"),
                keys = eq(
                    listOf(
                        KeyWithHash(FlipperFilePath("nfc", "from_flipper.nfc"), "FLIPPERHASH")
                    )
                ),
                diffSource = eq(DiffSource.FLIPPER)
            )
        } returns listOf(
            KeyDiff(
                KeyWithHash(FlipperFilePath("nfc", "from_flipper.nfc"), "FLIPPERHASH"),
                KeyAction.ADD,
                DiffSource.FLIPPER
            )
        )

        underTest.syncFolder(FlipperKeyType.NFC, detailedProgressWrapperTrackerStub())

        coVerify {
            keyDiffApplier.applyDiffs(
                diffWithAndroid = eq(
                    listOf(
                        KeyDiff(
                            KeyWithHash(FlipperFilePath("nfc", "from_android.nfc"), "ANDROIDHASH"),
                            KeyAction.ADD,
                            DiffSource.ANDROID
                        )
                    )
                ),
                diffWithFlipper = eq(
                    listOf(
                        KeyDiff(
                            KeyWithHash(FlipperFilePath("nfc", "from_flipper.nfc"), "FLIPPERHASH"),
                            KeyAction.ADD,
                            DiffSource.FLIPPER
                        )
                    )
                ),
                tracker = any()
            )
        }

        coVerify {
            manifestRepository.updateManifest(
                folder = eq(FlipperKeyType.NFC.flipperDir),
                keys = eq(
                    listOf(
                        KeyWithHash(FlipperFilePath("nfc", "from_android.nfc"), "ANDROIDHASH"),
                        KeyWithHash(FlipperFilePath("nfc", "from_flipper.nfc"), "FLIPPERHASH"),
                    )
                )
            )
        }
    }
}
