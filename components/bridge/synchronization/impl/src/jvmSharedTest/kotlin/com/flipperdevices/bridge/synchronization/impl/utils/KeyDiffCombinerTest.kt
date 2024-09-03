package com.flipperdevices.bridge.synchronization.impl.utils

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import org.junit.Assert
import org.junit.Test

class KeyDiffCombinerTest {
    @Test
    fun `two similar file return empty`() {
        val fromFlipper = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel1.ir"
                    ),
                    hash = "SIMILAR_HASH"
                ),
                action = KeyAction.ADD,
                source = DiffSource.FLIPPER
            )
        )
        val fromAndroid = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel1.ir"
                    ),
                    hash = "SIMILAR_HASH"
                ),
                action = KeyAction.ADD,
                source = DiffSource.ANDROID
            )
        )

        val actual = KeyDiffCombiner.combineKeyDiffs(fromFlipper, fromAndroid)

        Assert.assertEquals(actual, emptyList<KeyDiff>())
    }

    @Test
    fun `add file from flipper, than android`() {
        val fromFlipper = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel2.ir"
                    ),
                    hash = "DIFFERENT_HASH_2"
                ),
                action = KeyAction.ADD,
                source = DiffSource.FLIPPER
            )
        )
        val fromAndroid = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel1.ir"
                    ),
                    hash = "DIFFERENT_HASH_1"
                ),
                action = KeyAction.ADD,
                source = DiffSource.ANDROID
            )
        )

        val actual = KeyDiffCombiner.combineKeyDiffs(fromFlipper, fromAndroid)

        Assert.assertEquals(
            listOf(
                KeyDiff(
                    newHash = KeyWithHash(
                        FlipperFilePath(
                            "infrared",
                            "TCL_UnknownModel2.ir"
                        ),
                        hash = "DIFFERENT_HASH_2"
                    ),
                    action = KeyAction.ADD,
                    source = DiffSource.FLIPPER
                ),
                KeyDiff(
                    newHash = KeyWithHash(
                        FlipperFilePath(
                            "infrared",
                            "TCL_UnknownModel1.ir"
                        ),
                        hash = "DIFFERENT_HASH_1"
                    ),
                    action = KeyAction.ADD,
                    source = DiffSource.ANDROID
                )
            ),
            actual
        )
    }

    @Test
    fun `add file from android, than flipper`() {
        val fromFlipper = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel2.ir"
                    ),
                    hash = "DIFFERENT_HASH_2"
                ),
                action = KeyAction.ADD,
                source = DiffSource.FLIPPER
            )
        )
        val fromAndroid = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel1.ir"
                    ),
                    hash = "DIFFERENT_HASH_1"
                ),
                action = KeyAction.ADD,
                source = DiffSource.ANDROID
            )
        )

        val actual = KeyDiffCombiner.combineKeyDiffs(fromAndroid, fromFlipper)

        Assert.assertEquals(
            listOf(
                KeyDiff(
                    newHash = KeyWithHash(
                        FlipperFilePath(
                            "infrared",
                            "TCL_UnknownModel1.ir"
                        ),
                        hash = "DIFFERENT_HASH_1"
                    ),
                    action = KeyAction.ADD,
                    source = DiffSource.ANDROID
                ),
                KeyDiff(
                    newHash = KeyWithHash(
                        FlipperFilePath(
                            "infrared",
                            "TCL_UnknownModel2.ir"
                        ),
                        hash = "DIFFERENT_HASH_2"
                    ),
                    action = KeyAction.ADD,
                    source = DiffSource.FLIPPER
                )
            ),
            actual
        )
    }

    @Test
    fun `two similar file delete`() {
        val fromFlipper = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel1.ir"
                    ),
                    hash = "DIFFERENT_HASH_2"
                ),
                action = KeyAction.DELETED,
                source = DiffSource.FLIPPER
            )
        )
        val fromAndroid = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel1.ir"
                    ),
                    hash = "DIFFERENT_HASH_1"
                ),
                action = KeyAction.DELETED,
                source = DiffSource.ANDROID
            )
        )

        val actual = KeyDiffCombiner.combineKeyDiffs(fromFlipper, fromAndroid)

        Assert.assertEquals(actual, emptyList<KeyDiff>())
    }

    @Test
    fun `two similar shadow file add`() {
        val fromFlipper = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel1.shd"
                    ),
                    hash = "DIFFERENT_HASH_2"
                ),
                action = KeyAction.ADD,
                source = DiffSource.FLIPPER
            )
        )
        val fromAndroid = listOf(
            KeyDiff(
                newHash = KeyWithHash(
                    FlipperFilePath(
                        "infrared",
                        "TCL_UnknownModel1.shd"
                    ),
                    hash = "DIFFERENT_HASH_1"
                ),
                action = KeyAction.DELETED,
                source = DiffSource.ANDROID
            )
        )

        val actual = KeyDiffCombiner.combineKeyDiffs(fromFlipper, fromAndroid)

        Assert.assertEquals(
            actual,
            listOf(
                KeyDiff(
                    KeyWithHash(
                        FlipperFilePath(
                            "infrared",
                            "TCL_UnknownModel1.shd"
                        ),
                        hash = "DIFFERENT_HASH_2"
                    ),
                    action = KeyAction.ADD,
                    source = DiffSource.FLIPPER
                )
            )
        )
    }
}
