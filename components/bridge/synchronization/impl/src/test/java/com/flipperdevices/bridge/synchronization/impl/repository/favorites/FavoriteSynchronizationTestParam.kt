package com.flipperdevices.bridge.synchronization.impl.repository.favorites

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash

data class FavoriteSynchronizationTestParam(
    val initialFavoriteManifest: List<FlipperFilePath> = emptyList(),
    val initialFlipperFavoriteManifest: List<FlipperFilePath> = initialFavoriteManifest,
    val flipperFavorites: List<FlipperFilePath> = emptyList(),
    val androidFavorites: List<FlipperFilePath> = emptyList(),
    val expectedDiffOnFlipper: List<KeyDiff>,
    val expectedFavoritesOnAndroid: List<FlipperFilePath>,
    val expectedFavoritesOnAndroidManifest: List<FlipperFilePath> = expectedFavoritesOnAndroid,
    val expectedFavoritesOnFlipperManifest: List<FlipperFilePath> = expectedFavoritesOnAndroid
)

val testRuns = listOf(
    FavoriteSynchronizationTestParam(
        flipperFavorites = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        ),
        expectedDiffOnFlipper = emptyList(),
        expectedFavoritesOnAndroid = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        )

    ),
    FavoriteSynchronizationTestParam(
        androidFavorites = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        ),
        expectedDiffOnFlipper = listOf(
            KeyDiff(
                KeyWithHash(FlipperFilePath("test", "test.ibtn"), ""),
                KeyAction.ADD,
                DiffSource.ANDROID
            ),
            KeyDiff(
                KeyWithHash(FlipperFilePath("test", "test2.nfc"), ""),
                KeyAction.ADD,
                DiffSource.ANDROID
            )
        ),
        expectedFavoritesOnAndroid = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        )

    ),
    FavoriteSynchronizationTestParam(
        flipperFavorites = listOf(
            FlipperFilePath("test", "test1.nfc"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc")
        ),
        androidFavorites = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        ),
        expectedDiffOnFlipper = listOf(
            KeyDiff(
                KeyWithHash(FlipperFilePath("test", "test.ibtn"), ""),
                KeyAction.ADD,
                DiffSource.ANDROID
            )
        ),
        expectedFavoritesOnAndroid = listOf(
            FlipperFilePath("test", "test1.nfc"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "test.ibtn")
        )

    ),
    FavoriteSynchronizationTestParam(
        initialFavoriteManifest = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc")
        ),
        flipperFavorites = listOf(
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc")
        ),
        androidFavorites = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test1.nfc")
        ),
        expectedDiffOnFlipper = listOf(
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
        ),
        expectedFavoritesOnAndroid = listOf(
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test1.nfc")
        )
    ),
    FavoriteSynchronizationTestParam(
        initialFavoriteManifest = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc")
        ),
        flipperFavorites = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        ),
        androidFavorites = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        ),
        expectedDiffOnFlipper = emptyList(),
        expectedFavoritesOnAndroid = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc")
        )
    ),
    FavoriteSynchronizationTestParam(
        flipperFavorites = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "notExistedKey2.nfc"),
            FlipperFilePath("test", "test4.nfc"),
            FlipperFilePath("test", "notExistedKey3.nfc"),
            FlipperFilePath("test", "test5.nfc")
        ),
        expectedDiffOnFlipper = emptyList(),
        expectedFavoritesOnAndroid = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "notExistedKey2.nfc"),
            FlipperFilePath("test", "test4.nfc"),
            FlipperFilePath("test", "notExistedKey3.nfc"),
            FlipperFilePath("test", "test5.nfc")
        ),
        expectedFavoritesOnAndroidManifest = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "test4.nfc"),
            FlipperFilePath("test", "test5.nfc")
        ),
        expectedFavoritesOnFlipperManifest = listOf(
            FlipperFilePath("test", "test.ibtn"),
            FlipperFilePath("test", "test2.nfc"),
            FlipperFilePath("test", "test3.nfc"),
            FlipperFilePath("test", "notExistedKey2.nfc"),
            FlipperFilePath("test", "test4.nfc"),
            FlipperFilePath("test", "notExistedKey3.nfc"),
            FlipperFilePath("test", "test5.nfc")
        )
    )
)
