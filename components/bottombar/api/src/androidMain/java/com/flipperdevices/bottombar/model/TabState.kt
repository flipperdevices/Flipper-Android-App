package com.flipperdevices.bottombar.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
@Suppress("VariableNaming", "LongParameterList")
sealed interface TabState {
    val text: String
    val selectedColor: Color
    val selectedColorIcon: Color
    val unselectedColor: Color
    val unselectedColorIcon: Color
    val textDotsAnimated: Boolean
    val notificationDotActive: Boolean

    @Immutable
    @Suppress("LongParameterList")
    data class Static(
        @DrawableRes val selectedIcon: Int,
        @DrawableRes val notSelectedIcon: Int,
        override val text: String,
        override val selectedColor: Color,
        override val selectedColorIcon: Color = selectedColor,
        override val unselectedColor: Color,
        override val unselectedColorIcon: Color = unselectedColor,
        override val textDotsAnimated: Boolean = false,
        override val notificationDotActive: Boolean = false
    ) : TabState

    @Immutable
    @Suppress("LongParameterList")
    data class Animated(
        @DrawableRes val selectedIcon: Int,
        @DrawableRes val selectedBackground: Int,
        @DrawableRes val notSelectedIcon: Int,
        @DrawableRes val notSelectedBackground: Int,
        override val text: String,
        override val selectedColor: Color,
        override val selectedColorIcon: Color = selectedColor,
        override val unselectedColor: Color,
        override val unselectedColorIcon: Color = unselectedColor,
        override val textDotsAnimated: Boolean = false,
        override val notificationDotActive: Boolean = false,
    ) : TabState
}
