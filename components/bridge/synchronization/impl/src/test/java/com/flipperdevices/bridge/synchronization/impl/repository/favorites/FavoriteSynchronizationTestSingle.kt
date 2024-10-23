package com.flipperdevices.bridge.synchronization.impl.repository.favorites

import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronization
import com.flipperdevices.bridge.synchronization.impl.repository.FavoriteSynchronizationImpl
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperFavoritesRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepositoryImpl
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestStorage
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestStorageImpl
import com.flipperdevices.bridge.synchronization.impl.utils.detailedProgressWrapperTrackerStub
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class FavoriteSynchronizationTestSingle {
    @get:Rule
    val folder = TemporaryFolder()

    private lateinit var underTest: FavoriteSynchronization
    private lateinit var favoriteApi: FavoriteApi
    private lateinit var manifestRepository: ManifestRepository
    private lateinit var manifestStorage: ManifestStorage
    private lateinit var flipperStorage: FlipperKeyStorage
    private lateinit var favoritesRepository: FlipperFavoritesRepository

    @Before
    fun setUp() {
        favoriteApi = mockk()
        flipperStorage = mockk()
        favoritesRepository = mockk()

        val testFolder = folder.newFolder("manifest")
        val context = mockk<Context> {
            every { filesDir } returns testFolder
        }
        manifestStorage = ManifestStorageImpl(context)
        manifestRepository = ManifestRepositoryImpl(manifestStorage)

        underTest = FavoriteSynchronizationImpl(
            favoriteApi = favoriteApi,
            manifestRepository = manifestRepository,
            flipperStorage = flipperStorage,
            favoritesRepository = favoritesRepository
        )
    }

    @Test
    @Suppress("LongMethod")
    fun `on similar manifest nothing happes`() = runTest {
        val manifestFavorites = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "test4.nfc"),
            FlipperFilePath("test", "test5.nfc")
        )
        val manifestFlipper = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "notExistedKey2.nfc"),
            FlipperFilePath("test", "test4.nfc"),
            FlipperFilePath("test", "notExistedKey3.nfc"),
            FlipperFilePath("test", "test5.nfc")
        )
        manifestStorage.update {
            it.copy(
                favorites = manifestFavorites,
                favoritesFromFlipper = manifestFlipper
            )
        }

        val favoritesFromFlipper = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "notExistedKey2.nfc"),
            FlipperFilePath("test", "test4.nfc"),
            FlipperFilePath("test", "notExistedKey3.nfc"),
            FlipperFilePath("test", "test5.nfc")
        )
        val favoritesFromAndroid = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "test4.nfc"),
            FlipperFilePath("test", "test5.nfc")
        )
        coEvery { favoritesRepository.getFavorites(any()) } returns favoritesFromFlipper
        coEvery { favoriteApi.getFavorites() } returns favoritesFromAndroid.map { filePath ->
            FlipperKey(
                mainFile = FlipperFile(filePath, FlipperKeyContent.RawData(byteArrayOf())),
                synchronized = false,
                deleted = false
            )
        }

        underTest.syncFavorites(detailedProgressWrapperTrackerStub())

        coVerify(exactly = 0) {
            favoritesRepository.applyDiff(
                flipperKeyStorage = any(),
                oldFavorites = any(),
                favoritesDiff = any()
            )
        }

        coVerify(exactly = 0) {
            favoriteApi.updateFavorites(any())
        }

        val manifestFile = manifestStorage.load()
        Assert.assertNotNull(manifestFile)
        Assert.assertEquals(manifestFavorites, manifestFile!!.favorites)
        Assert.assertEquals(manifestFlipper, manifestFile.favoritesFromFlipper)
    }
}
