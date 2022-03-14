package com.flipperdevices.screenstreaming.impl.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.screenstreaming.impl.R

enum class ButtonEnum(
    @DrawableRes val icon: Int,
    @StringRes val description: Int,
    val key: Gui.InputKey
) {
    LEFT(R.drawable.ic_arrow_left, R.string.control_left, Gui.InputKey.LEFT),
    RIGHT(R.drawable.ic_arrow_right, R.string.control_right, Gui.InputKey.RIGHT),
    UP(R.drawable.ic_arrow_up, R.string.control_up, Gui.InputKey.UP),
    DOWN(R.drawable.ic_arrow_down, R.string.control_down, Gui.InputKey.DOWN),
    OK(R.drawable.ic_circle, R.string.control_ok, Gui.InputKey.OK),
    BACK(R.drawable.ic_back, R.string.control_back, Gui.InputKey.BACK)
}
