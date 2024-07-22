package com.flipperdevices.analytics.shake2report.impl.composable.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import com.flipperdevices.core.ui.theme.LocalPallet

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ComposableCheckBox(
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    /*
    Disable padding for checkbox
    https://stackoverflow.com/a/71609165
     */
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckChange,
            colors = CheckboxColors(
                checkedCheckmarkColor = LocalPallet.current.accent,
                checkedBorderColor = LocalPallet.current.reportBorder,
                uncheckedBorderColor = LocalPallet.current.reportBorder,
            ),
            interactionSource = remember { MutableInteractionSource() }
        )
    }
}

private const val BOX_IN_DURATION = 50
private const val BOX_OUT_DURATION = 100

/**
 * Copy paste from [CheckboxDefaults.colors] because we can't override [DefaultCheckboxColors]
 */
@Stable
@Suppress("LongParameterList")
class CheckboxColors(
    private val checkedCheckmarkColor: Color = Color.Transparent,
    private val uncheckedCheckmarkColor: Color = Color.Transparent,
    private val checkedBoxColor: Color = Color.Transparent,
    private val uncheckedBoxColor: Color = Color.Transparent,
    private val checkedBorderColor: Color = Color.Transparent,
    private val uncheckedBorderColor: Color = Color.Transparent,

    private val disabledBorderColor: Color = Color.Transparent,
    private val disabledIndeterminateBorderColor: Color = Color.Transparent,
    private val disabledCheckedBoxColor: Color = Color.Transparent,
    private val disabledUncheckedBoxColor: Color = Color.Transparent,
    private val disabledIndeterminateBoxColor: Color = Color.Transparent,
) : CheckboxColors {

    @Composable
    override fun checkmarkColor(state: ToggleableState): State<Color> {
        val target = if (state == ToggleableState.Off) {
            uncheckedCheckmarkColor
        } else {
            checkedCheckmarkColor
        }

        val duration = getDuration(state == ToggleableState.Off)
        return animateColorAsState(target, tween(durationMillis = duration))
    }

    @Composable
    override fun boxColor(enabled: Boolean, state: ToggleableState): State<Color> {
        val target = if (enabled) {
            when (state) {
                ToggleableState.On, ToggleableState.Indeterminate -> checkedBoxColor
                ToggleableState.Off -> uncheckedBoxColor
            }
        } else {
            when (state) {
                ToggleableState.On -> disabledCheckedBoxColor
                ToggleableState.Indeterminate -> disabledIndeterminateBoxColor
                ToggleableState.Off -> disabledUncheckedBoxColor
            }
        }

        // If not enabled 'snap' to the disabled state, as there should be no animations between
        // enabled / disabled.
        return if (enabled) {
            val duration = getDuration(state == ToggleableState.Off)
            animateColorAsState(target, tween(durationMillis = duration))
        } else {
            rememberUpdatedState(target)
        }
    }

    @Composable
    override fun borderColor(enabled: Boolean, state: ToggleableState): State<Color> {
        val target = if (enabled) {
            when (state) {
                ToggleableState.On, ToggleableState.Indeterminate -> checkedBorderColor
                ToggleableState.Off -> uncheckedBorderColor
            }
        } else {
            when (state) {
                ToggleableState.Indeterminate -> disabledIndeterminateBorderColor
                ToggleableState.On, ToggleableState.Off -> disabledBorderColor
            }
        }

        // If not enabled 'snap' to the disabled state, as there should be no animations between
        // enabled / disabled.
        return if (enabled) {
            val duration = getDuration(state == ToggleableState.Off)
            animateColorAsState(target, tween(durationMillis = duration))
        } else {
            rememberUpdatedState(target)
        }
    }

    private fun getDuration(flag: Boolean) = if (flag) BOX_IN_DURATION else BOX_OUT_DURATION
}
