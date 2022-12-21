package com.flipperdevices.bridge.synchronization.impl.repository

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperFavoritesRepository
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestRepositoryImpl
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestStorage
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestStorageImpl
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

@RunWith(AndroidJUnit4::class)
class FavoriteSynchronizationTest {
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
        favoriteApi = mockk(relaxUnitFun = true)
        flipperStorage = mockk()
        favoritesRepository = mockk {
            coEvery { applyDiff(any(), any(), any()) } coAnswers {
                val oldFavorites = (args[1] as List<*>).map { it as FlipperFilePath }
                val favoritesDiff = (args[2] as List<*>).map { it as KeyDiff }
                val resultFavoritesList = ArrayList(oldFavorites)
                for (diff in favoritesDiff) {
                    when (diff.action) {
                        KeyAction.ADD -> resultFavoritesList.add(diff.newHash.keyPath)
                        KeyAction.MODIFIED -> resultFavoritesList.add(diff.newHash.keyPath)
                        KeyAction.DELETED -> resultFavoritesList.remove(diff.newHash.keyPath)
                    }
                }
                return@coAnswers resultFavoritesList
            }
        }

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
    fun `receive favorites from flipper first time`() = runTest {
        val favoritesFromFlipper = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        )
        coEvery { favoritesRepository.getFavorites(any()) } returns favoritesFromFlipper
        coEvery { favoriteApi.getFavorites() } returns emptyList()

        underTest.syncFavorites()

        coVerify {
            favoritesRepository.applyDiff(
                flipperKeyStorage = eq(flipperStorage),
                oldFavorites = eq(favoritesFromFlipper),
                favoritesDiff = eq(emptyList())
            )
        }

        coVerify {
            favoriteApi.updateFavorites(eq(favoritesFromFlipper.map {
                FlipperKeyPath(it, false)
            }))
        }

        val manifestFile = manifestStorage.load()
        Assert.assertNotNull(manifestFile)
        Assert.assertEquals(favoritesFromFlipper, manifestFile!!.favorites)
        Assert.assertEquals(favoritesFromFlipper, manifestFile.favoritesFromFlipper)
    }

    @Test
    fun `receive favorites from android first time`() = runTest {
        val favoritesFromAndroid = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        )
        coEvery { favoritesRepository.getFavorites(any()) } returns emptyList()
        coEvery { favoriteApi.getFavorites() } returns favoritesFromAndroid.map { filePath ->
            FlipperKey(
                mainFile = FlipperFile(filePath, FlipperKeyContent.RawData(byteArrayOf())),
                synchronized = false,
                deleted = false
            )
        }

        underTest.syncFavorites()

        coVerify {
            favoritesRepository.applyDiff(
                flipperKeyStorage = eq(flipperStorage),
                oldFavorites = eq(emptyList()),
                favoritesDiff = eq(favoritesFromAndroid.map {
                    KeyDiff(
                        KeyWithHash(it, ""),
                        KeyAction.ADD,
                        DiffSource.ANDROID
                    )
                })
            )
        }

        coVerify {
            favoriteApi.updateFavorites(eq(favoritesFromAndroid.map {
                FlipperKeyPath(it, false)
            }))
        }

        val manifestFile = manifestStorage.load()
        Assert.assertNotNull(manifestFile)
        Assert.assertEquals(favoritesFromAndroid, manifestFile!!.favorites)
        Assert.assertEquals(favoritesFromAndroid, manifestFile.favoritesFromFlipper)
    }

    @Test
    fun `combine favorites without conflicts and empty manifest`() = runTest {
        val favoritesFromFlipper = listOf(
            FlipperFilePath("test", "test1.nfc"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc")
        )
        val favoritesFromAndroid = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        )
        coEvery { favoritesRepository.getFavorites(any()) } returns favoritesFromFlipper
        coEvery { favoriteApi.getFavorites() } returns favoritesFromAndroid.map { filePath ->
            FlipperKey(
                mainFile = FlipperFile(filePath, FlipperKeyContent.RawData(byteArrayOf())),
                synchronized = false,
                deleted = false
            )
        }

        underTest.syncFavorites()

        coVerify {
            favoritesRepository.applyDiff(
                flipperKeyStorage = eq(flipperStorage),
                oldFavorites = eq(favoritesFromFlipper),
                favoritesDiff = eq(
                    listOf(
                        KeyDiff(
                            KeyWithHash(FlipperFilePath("test", "test.ibtn"), ""),
                            KeyAction.ADD,
                            DiffSource.ANDROID
                        )
                    )
                )
            )
        }

        val resultList = listOf(
            FlipperFilePath("test", "test1.nfc"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "test.ibtn"),
        )

        coVerify {
            favoriteApi.updateFavorites(eq(resultList.map {
                FlipperKeyPath(it, false)
            }))
        }

        val manifestFile = manifestStorage.load()
        Assert.assertNotNull(manifestFile)
        Assert.assertEquals(resultList, manifestFile!!.favorites)
        Assert.assertEquals(resultList, manifestFile.favoritesFromFlipper)
    }


    @Test
    fun `combine favorites without conflicts and prefilled manifest`() = runTest {
        val initialManifest = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc")
        )
        manifestStorage.update(favorites = initialManifest, favoritesOnFlipper = initialManifest)

        val favoritesFromFlipper = listOf(
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc")
        )
        val favoritesFromAndroid = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test1.nfc")
        )
        coEvery { favoritesRepository.getFavorites(any()) } returns favoritesFromFlipper
        coEvery { favoriteApi.getFavorites() } returns favoritesFromAndroid.map { filePath ->
            FlipperKey(
                mainFile = FlipperFile(filePath, FlipperKeyContent.RawData(byteArrayOf())),
                synchronized = false,
                deleted = false
            )
        }

        underTest.syncFavorites()

        coVerify {
            favoritesRepository.applyDiff(
                flipperKeyStorage = eq(flipperStorage),
                oldFavorites = eq(favoritesFromFlipper),
                favoritesDiff = eq(
                    listOf(
                        KeyDiff(
                            KeyWithHash(FlipperFilePath("test", "test3.nfc"), ""),
                            KeyAction.DELETED,
                            DiffSource.ANDROID
                        ),
                        KeyDiff(
                            KeyWithHash(FlipperFilePath("test", "test1.nfc"), ""),
                            KeyAction.ADD,
                            DiffSource.ANDROID
                        )
                    )
                )
            )
        }

        val resultList = listOf(
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test1.nfc")
        )

        coVerify {
            favoriteApi.updateFavorites(eq(resultList.map {
                FlipperKeyPath(it, false)
            }))
        }

        val manifestFile = manifestStorage.load()
        Assert.assertNotNull(manifestFile)
        Assert.assertEquals(resultList, manifestFile!!.favorites)
        Assert.assertEquals(resultList, manifestFile.favoritesFromFlipper)
    }

    @Test
    fun `combine favorites with conflicts`() = runTest {

    }

    @Test
    fun `receive favorites with path that are missing`() = runTest {

    }

    @Test
    fun `combine favorites with path that are missing`() = runTest {

    }

    @Test
    fun `receive favorites after creation flipper favorites manifest`() = runTest {

    }
}
