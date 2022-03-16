package com.flipperdevices.bottombar.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes

private const val MAGIC_NUMBER = 31

@Suppress("LongParameterList")
sealed class TabState private constructor(
    @StringRes val textId: Int,
    @ColorRes val selectedColor: Int,
    @ColorRes val selectedColorIcon: Int,
    @ColorRes val unselectedColor: Int,
    @ColorRes val unselectedColorIcon: Int,
    val textDotsAnimated: Boolean
) {
    class Static(
        @DrawableRes val selectedIcon: Int,
        @DrawableRes val notSelectedIcon: Int,
        @StringRes textId: Int,
        @ColorRes selectedColor: Int,
        @ColorRes selectedColorIcon: Int = selectedColor,
        @ColorRes unselectedColor: Int,
        @ColorRes unselectedColorIcon: Int = unselectedColor,
        textDotsAnimated: Boolean = false
    ) : TabState(
        textId,
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
        @StringRes textId: Int,
        @ColorRes selectedColor: Int,
        @ColorRes unselectedColor: Int,
        textDotsAnimated: Boolean = false
    ) : TabState(
        textId,
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

        if (textId != other.textId) return false
        if (selectedColor != other.selectedColor) return false
        if (selectedColorIcon != other.selectedColorIcon) return false
        if (unselectedColor != other.unselectedColor) return false
        if (unselectedColorIcon != other.unselectedColorIcon) return false
        if (textDotsAnimated != other.textDotsAnimated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = textId
        result = MAGIC_NUMBER * result + selectedColor
        result = MAGIC_NUMBER * result + selectedColorIcon
        result = MAGIC_NUMBER * result + unselectedColor
        result = MAGIC_NUMBER * result + unselectedColorIcon
        result = MAGIC_NUMBER * result + textDotsAnimated.hashCode()
        return result
    }
}
