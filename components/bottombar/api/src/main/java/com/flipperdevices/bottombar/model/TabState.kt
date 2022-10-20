package com.flipperdevices.bottombar.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

private const val MAGIC_NUMBER = 31

@Suppress("VariableNaming")
sealed class TabState private constructor(
    val text: String,
    val selectedColor: Color,
    val selectedColorIcon: Color,
    val unselectedColor: Color,
    val unselectedColorIcon: Color,
    val textDotsAnimated: Boolean,
    val notificationDotActive: Boolean
) {
    @Suppress("LongParameterList")
    class Static(
        @DrawableRes val selectedIcon: Int,
        @DrawableRes val notSelectedIcon: Int,
        text: String,
        selectedColor: Color,
        selectedColorIcon: Color = selectedColor,
        unselectedColor: Color,
        unselectedColorIcon: Color = unselectedColor,
        textDotsAnimated: Boolean = false,
        notificationDotActive: Boolean = false
    ) : TabState(
        text,
        selectedColor,
        selectedColorIcon,
        unselectedColor,
        unselectedColorIcon,
        textDotsAnimated,
        notificationDotActive
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as Static

            if (selectedIcon != other.selectedIcon) return false
            if (notSelectedIcon != other.notSelectedIcon) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = MAGIC_NUMBER * result + selectedIcon
            result = MAGIC_NUMBER * result + notSelectedIcon
            return result
        }
    }

    @Suppress("LongParameterList")
    class Animated(
        @DrawableRes val selectedIcon: Int,
        @DrawableRes val selectedBackground: Int,
        @DrawableRes val notSelectedIcon: Int,
        @DrawableRes val notSelectedBackground: Int,
        text: String,
        selectedColor: Color,
        unselectedColor: Color,
        textDotsAnimated: Boolean = false,
        notificationDotActive: Boolean = false
    ) : TabState(
        text,
        selectedColor,
        selectedColor,
        unselectedColor,
        unselectedColor,
        textDotsAnimated,
        notificationDotActive
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as Animated

            if (selectedIcon != other.selectedIcon) return false
            if (notSelectedIcon != other.notSelectedIcon) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = MAGIC_NUMBER * result + selectedIcon
            result = MAGIC_NUMBER * result + notSelectedIcon
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabState) return false

        if (text != other.text) return false
        if (selectedColor != other.selectedColor) return false
        if (selectedColorIcon != other.selectedColorIcon) return false
        if (unselectedColor != other.unselectedColor) return false
        if (unselectedColorIcon != other.unselectedColorIcon) return false
        if (textDotsAnimated != other.textDotsAnimated) return false
        if (notificationDotActive != other.notificationDotActive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = MAGIC_NUMBER * result + selectedColor.hashCode()
        result = MAGIC_NUMBER * result + selectedColorIcon.hashCode()
        result = MAGIC_NUMBER * result + unselectedColor.hashCode()
        result = MAGIC_NUMBER * result + unselectedColorIcon.hashCode()
        result = MAGIC_NUMBER * result + textDotsAnimated.hashCode()
        result = MAGIC_NUMBER * result + notificationDotActive.hashCode()
        return result
    }
}
