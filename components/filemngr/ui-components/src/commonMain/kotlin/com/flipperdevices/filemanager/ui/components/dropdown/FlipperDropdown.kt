package com.flipperdevices.filemanager.ui.components.dropdown

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun FDropdownItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    isActive: Boolean = true,
    colorText: Color = LocalPalletV2.current.action.blackAndWhite.text.default,
) {
    DropdownMenuItem(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        onClick = {
            if (isActive) {
                onClick()
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.invoke()
            Text(
                text = text,
                style = LocalTypography.current.bodyM14,
                color = if (isActive) {
                    colorText
                } else {
                    LocalPalletV2.current.action.blackAndWhite.text.disabled
                }
            )
        }
    }
}

@Composable
fun IconDropdownItem(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    colorText: Color = LocalPalletV2.current.action.blackAndWhite.text.default,
    colorIcon: Color = LocalPalletV2.current.action.blackAndWhite.icon.default,
) {
    FDropdownItem(
        modifier = modifier,
        onClick = onClick,
        colorText = animateColorAsState(
            if (isActive) {
                colorText
            } else {
                LocalPalletV2.current.action.blackAndWhite.text.disabled
            }
        ).value,
        text = text,
        isActive = isActive,
        icon = {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painter,
                tint = animateColorAsState(
                    if (isActive) {
                        colorIcon
                    } else {
                        LocalPalletV2.current.action.blackAndWhite.icon.disabled
                    }
                ).value,
                contentDescription = null
            )
        }
    )
}

@Composable
fun TextDropdownItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    colorText: Color = LocalPalletV2.current.action.blackAndWhite.text.default,
) {
    FDropdownItem(
        modifier = modifier,
        onClick = onClick,
        colorText = animateColorAsState(
            if (isActive) {
                colorText
            } else {
                LocalPalletV2.current.action.blackAndWhite.text.disabled
            }
        ).value,
        text = text,
        isActive = isActive,
    )
}

@Composable
fun RadioDropdownItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    colorText: Color = LocalPalletV2.current.action.blackAndWhite.text.default,
) {
    FDropdownItem(
        modifier = modifier,
        onClick = onClick,
        colorText = colorText,
        text = text,
        isActive = isEnabled,
        icon = {
            RadioButton(
                modifier = Modifier.size(20.dp),
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = LocalPalletV2.current.action.brand.background.primary.default,
                    disabledColor = LocalPalletV2.current.action.brand.background.primary.disabled,
                )
            )
        }
    )
}
