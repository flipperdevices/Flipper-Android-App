package com.flipperdevices.screenstreaming.impl.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.screenstreaming.impl.R

enum class ButtonEnum(
    @DrawableRes val icon: Int,
    @StringRes val description: Int,
    val key: Gui.InputKey
) {
    LEFT(R.drawable.ic_control_left, R.string.control_left, Gui.InputKey.LEFT),
    RIGHT(R.drawable.ic_control_right, R.string.control_right, Gui.InputKey.RIGHT),
    UP(R.drawable.ic_control_up, R.string.control_up, Gui.InputKey.UP),
    DOWN(R.drawable.ic_control_down, R.string.control_down, Gui.InputKey.DOWN),
    OK(R.drawable.ic_control_ok, R.string.control_ok, Gui.InputKey.OK),
    BACK(R.drawable.ic_control_back, R.string.control_back, Gui.InputKey.BACK);

    @Composable
    fun getAnimIcon(): Painter {
        val isLight = MaterialTheme.colors.isLight
        return when (this) {
            LEFT -> {
                if (isLight) {
                    painterResource(id = R.drawable.ic_anim_left_button_light)
                } else {
                    painterResource(id = R.drawable.ic_anim_left_button_dark)
                }
            }
            RIGHT -> {
                if (isLight) {
                    painterResource(id = R.drawable.ic_anim_right_button_light)
                } else {
                    painterResource(id = R.drawable.ic_anim_right_button_dark)
                }
            }
            UP -> {
                if (isLight) {
                    painterResource(id = R.drawable.ic_anim_up_button_light)
                } else {
                    painterResource(id = R.drawable.ic_anim_up_button_dark)
                }
            }
            DOWN -> {
                if (isLight) {
                    painterResource(id = R.drawable.ic_anim_down_button_light)
                } else {
                    painterResource(id = R.drawable.ic_anim_down_button_dark)
                }
            }
            OK -> {
                if (isLight) {
                    painterResource(id = R.drawable.ic_anim_ok_button_light)
                } else {
                    painterResource(id = R.drawable.ic_anim_ok_button_dark)
                }
            }
            BACK -> {
                if (isLight) {
                    painterResource(id = R.drawable.ic_anim_back_button_light)
                } else {
                    painterResource(id = R.drawable.ic_anim_back_button_dark)
                }
            }
        }
    }
}
