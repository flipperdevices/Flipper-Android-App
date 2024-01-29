package com.flipperdevices.core.ui.ktx.image

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

/**
 * Need for workaround Jetpack Compose Issue
 * https://issuetracker.google.com/issues/202863204
 * https://stackoverflow.com/q/70171212/5272499
 */
@Composable
fun painterResourceByKey(@DrawableRes id: Int, key: Int = id): Painter {
    return key(key) { painterResource(id) }
}
