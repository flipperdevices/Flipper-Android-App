package com.flipperdevices.screenstreaming.impl.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.screenstreaming.impl.R

enum class ButtonEnum(
    @DrawableRes val icon: Int,
    @StringRes val description: Int,
    val key: Gui.InputKey?
) {
    LEFT(DesignSystem.drawable.ic_arrow_left, R.string.control_left, Gui.InputKey.LEFT),
    RIGHT(DesignSystem.drawable.ic_arrow_right, R.string.control_right, Gui.InputKey.RIGHT),
    UP(DesignSystem.drawable.ic_arrow_up, R.string.control_up, Gui.InputKey.UP),
    DOWN(DesignSystem.drawable.ic_arrow_down, R.string.control_down, Gui.InputKey.DOWN),
    OK(DesignSystem.drawable.ic_circle, R.string.control_ok, Gui.InputKey.OK),
    BACK(DesignSystem.drawable.ic_back_arrow, R.string.control_back, Gui.InputKey.BACK),
    UNLOCK(DesignSystem.drawable.ic_double_back, R.string.control_unlock, Gui.InputKey.BACK),
    SCREENSHOT(DesignSystem.drawable.ic_screenshot, R.string.control_screenshot, null)
}
