package com.flipperdevices.core.ui.dialog.composable.multichoice

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel.Builder

fun Builder.setTitle(
    @StringRes textId: Int
): Builder = setTitle(composableText = { stringResource(textId) })

fun Builder.setDescription(
    @StringRes textId: Int
): Builder = setDescription(composableText = { stringResource(textId) })

fun Builder.addButton(
    @StringRes textId: Int,
    onClick: () -> Unit,
    isActive: Boolean = false
): Builder = addButton(
    textComposable = { stringResource(textId) },
    onClick = onClick,
    isActive = isActive
)

fun Builder.addButton(
    @StringRes textId: Int,
    onClick: () -> Unit,
    textColor: Color
): Builder = addButton(
    textComposable = { stringResource(textId) },
    onClick = onClick,
    textColor = textColor
)
