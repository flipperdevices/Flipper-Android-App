package com.flipperdevices.bridge.synchronization.impl.repository.favorites

import android.content.Context
import android.os.Build
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
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
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class FavoriteSynchronizationTest(
    val param: FavoriteSynchronizationTestParam
) {
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
        favoriteApi = mockk {
            coEvery { updateFavorites(any()) } coAnswers {
                (args[0] as List<*>).map { it as FlipperKeyPath }.filterNot {
                    it.path.nameWithoutExtension.startsWith("notExistedKey")
                }
            }
        }
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
    fun `universal manifest test`() = runTest {
        manifestStorage.update {
            it.copy(
                favorites = param.initialFavoriteManifest,
                favoritesFromFlipper = param.initialFlipperFavoriteManifest
            )
        }

        val favoritesFromFlipper = param.flipperFavorites
        val favoritesFromAndroid = param.androidFavorites
        coEvery { favoritesRepository.getFavorites(any()) } returns favoritesFromFlipper
        coEvery { favoriteApi.getFavorites() } returns favoritesFromAndroid.map { filePath ->
            FlipperKey(
                mainFile = FlipperFile(filePath, FlipperKeyContent.RawData(byteArrayOf())),
                synchronized = false,
                deleted = false
            )
        }

        underTest.syncFavorites(detailedProgressWrapperTrackerStub())

        if (param.expectedDiffOnFlipper != null) {
            coVerify {
                favoritesRepository.applyDiff(
                    flipperKeyStorage = eq(flipperStorage),
                    oldFavorites = eq(favoritesFromFlipper),
                    favoritesDiff = eq(param.expectedDiffOnFlipper)
                )
            }
        }

        if (param.expectedFavoritesOnAndroid != null) {
            coVerify {
                favoriteApi.updateFavorites(
                    eq(
                        param.expectedFavoritesOnAndroid.map {
                            FlipperKeyPath(it, false)
                        }
                    )
                )
            }
        }

        val manifestFile = manifestStorage.load()
        Assert.assertNotNull(manifestFile)
        Assert.assertEquals(param.expectedFavoritesOnAndroidManifest, manifestFile!!.favorites)
        Assert.assertEquals(
            param.expectedFavoritesOnFlipperManifest,
            manifestFile.favoritesFromFlipper
        )
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data() = testRuns
    }
}
