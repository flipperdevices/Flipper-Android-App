package com.flipperdevices.bottombar.model

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

private const val MAGIC_NUMBER = 31

sealed class TabState private constructor(
    val text: String,
    val selectedColor: Color,
    val selectedColorIcon: Color,
    val unselectedColor: Color,
    val unselectedColorIcon: Color,
    val textDotsAnimated: Boolean
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
        textDotsAnimated: Boolean = false
    ) : TabState(
        text,
        selectedColor,
        selectedColorIcon,
        unselectedColor,
        unselectedColorIcon,
        textDotsAnimated
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

    class Animated(
        @RawRes val selectedIcon: Int,
        @RawRes val notSelectedIcon: Int,
        text: String,
        selectedColor: Color,
        unselectedColor: Color,
        textDotsAnimated: Boolean = false
    ) : TabState(
        text,
        selectedColor,
        selectedColor,
        unselectedColor,
        unselectedColor,
        textDotsAnimated
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
        if (javaClass != other?.javaClass) return false

        other as TabState

        if (text != other.text) return false
        if (selectedColor != other.selectedColor) return false
        if (selectedColorIcon != other.selectedColorIcon) return false
        if (unselectedColor != other.unselectedColor) return false
        if (unselectedColorIcon != other.unselectedColorIcon) return false
        if (textDotsAnimated != other.textDotsAnimated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = MAGIC_NUMBER * result + selectedColor.toArgb()
        result = MAGIC_NUMBER * result + selectedColorIcon.toArgb()
        result = MAGIC_NUMBER * result + unselectedColor.toArgb()
        result = MAGIC_NUMBER * result + unselectedColorIcon.toArgb()
        result = MAGIC_NUMBER * result + textDotsAnimated.hashCode()
        return result
    }
}
