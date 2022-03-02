package com.flipperdevices.keyedit.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.keyedit.impl.R
import com.flipperdevices.keyedit.impl.model.SaveButtonState
import com.flipperdevices.keyscreen.shared.ComposableKeyScreenAppBar

@Composable
@Suppress("LongMethod")
fun ComposableEditAppBar(
    saveButtonState: SaveButtonState,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    ComposableKeyScreenAppBar(
        startBlock = {
            Text(
                modifier = it.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onBack
                ),
                text = stringResource(R.string.keyedit_bar_cancel),
                fontSize = 16.sp,
                color = colorResource(DesignSystem.color.black_40),
                fontWeight = FontWeight.W500
            )
        },
        centerBlock = {
            Text(
                modifier = it,
                text = stringResource(R.string.keyedit_bar_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.W800,
                color = colorResource(DesignSystem.color.black_88)
            )
        },
        endBlock = { ComposableSaveButton(it, saveButtonState, onSave) }
    )
}

private const val SAVE_BUTTON_SIZE_SP = 16
private val SAVE_BUTTON_TEXT_ID = R.string.keyedit_bar_save
private val SAVE_BUTTON_WEIGHT = FontWeight.W500

@Composable
private fun ComposableSaveButton(
    modifier: Modifier,
    saveButtonState: SaveButtonState,
    onSave: () -> Unit
) {
    when (saveButtonState) {
        SaveButtonState.ENABLED -> Text(
            modifier = modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onSave
                ),
            text = stringResource(SAVE_BUTTON_TEXT_ID),
            fontWeight = SAVE_BUTTON_WEIGHT,
            fontSize = SAVE_BUTTON_SIZE_SP.sp,
            color = colorResource(DesignSystem.color.accent_secondary)
        )
        SaveButtonState.DISABLED -> Text(
            modifier = modifier,
            text = stringResource(SAVE_BUTTON_TEXT_ID),
            fontWeight = SAVE_BUTTON_WEIGHT,
            fontSize = SAVE_BUTTON_SIZE_SP.sp,
            color = colorResource(DesignSystem.color.black_30)
        )
        SaveButtonState.IN_PROGRESS -> CircularProgressIndicator()
    }
}
