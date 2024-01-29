package com.flipperdevices.screenstreaming.impl.model

import androidx.annotation.DrawableRes
import com.flipperdevices.screenstreaming.impl.R
import java.util.UUID

data class FlipperButtonStackElement(
    val enum: ButtonAnimEnum,
    val uuid: UUID = UUID.randomUUID(),
)

enum class ButtonAnimEnum(@DrawableRes val lightId: Int, @DrawableRes val darkId: Int) {
    LEFT(R.drawable.ic_anim_left_button_light, R.drawable.ic_anim_left_button_dark),
    RIGHT(R.drawable.ic_anim_right_button_light, R.drawable.ic_anim_right_button_dark),
    UP(R.drawable.ic_anim_up_button_light, R.drawable.ic_anim_up_button_dark),
    DOWN(R.drawable.ic_anim_down_button_light, R.drawable.ic_anim_down_button_dark),
    OK(R.drawable.ic_anim_ok_button_light, R.drawable.ic_anim_ok_button_dark),
    BACK(R.drawable.ic_anim_back_button_light, R.drawable.ic_anim_back_button_dark),
    UNLOCK(R.drawable.ic_anim_unlock_light, R.drawable.ic_anim_unlock_dark)
}
