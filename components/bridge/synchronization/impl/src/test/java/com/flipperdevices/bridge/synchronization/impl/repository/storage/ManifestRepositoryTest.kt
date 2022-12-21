package com.flipperdevices.bridge.synchronization.impl.repository.storage

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ManifestRepositoryTest {
    private lateinit var underTest: ManifestRepository
    private lateinit var manifestStorage: ManifestStorage

    @Before
    fun setUp() {
        manifestStorage = mockk(relaxUnitFun = true) {
            coEvery { load() } returns null
        }
        underTest = ManifestRepositoryImpl(manifestStorage)
    }

    @Test
    fun `update keys`() = runTest {
        val expected = listOf(KeyWithHash(FlipperFilePath.DUMMY, "HASH"))
        underTest.updateManifest(keys = expected)

        coVerify { manifestStorage.update(keys = expected) }
    }

    @Test
    fun `update favorites`() = runTest {
        val expectedFavorites = listOf(FlipperFilePath("test", "test.ibtb"))
        val expectedFavoritesFromFlpr = listOf(
            FlipperFilePath("test", "test.ibtb"),
            FlipperFilePath("test", "test2.nfc")
        )
        underTest.updateManifest(
            favorites = expectedFavorites,
            favoritesOnFlipper = expectedFavoritesFromFlpr
        )

        coVerify {
            manifestStorage.update(
                favorites = expectedFavorites,
                favoritesOnFlipper = expectedFavoritesFromFlpr
            )
        }
    }

    @Test
    fun `compare keys with empty manifest`() = runTest {
        val expected = listOf(
            KeyWithHash(FlipperFilePath("test", "test.ibtn"), "HASH"),
            KeyWithHash(FlipperFilePath("test", "test2.nfc"), "HASH")
        )

        val actual = underTest.compareKeysWithManifest(expected, DiffSource.ANDROID)

        Assert.assertEquals(
            expected.map { KeyDiff(it, KeyAction.ADD, DiffSource.ANDROID) },
            actual
        )
    }

    @Test
    fun `compare favorites with empty manifest`() = runTest {
        val expected = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        )

        val actual = underTest.compareFavoritesWithManifest(expected)

        Assert.assertEquals(
            expected.map { KeyDiff(KeyWithHash(it, ""), KeyAction.ADD, DiffSource.ANDROID) },
            actual
        )
    }

    @Test
    fun `compare flipper favorites with empty manifest`() = runTest {
        val expected = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        )

        val actual = underTest.compareFlipperFavoritesWithManifest(expected)

        Assert.assertEquals(
            expected.map { KeyDiff(KeyWithHash(it, ""), KeyAction.ADD, DiffSource.FLIPPER) },
            actual
        )
    }
}
