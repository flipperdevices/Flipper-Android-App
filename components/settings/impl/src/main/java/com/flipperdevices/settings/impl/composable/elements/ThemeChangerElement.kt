package com.flipperdevices.settings.impl.composable.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.components.SimpleElement
import com.flipperdevices.core.ui.res.R as DesignSystem

private val themes = listOf(SelectedTheme.SYSTEM, SelectedTheme.DARK, SelectedTheme.LIGHT)

@Composable
fun ThemeChangerElement(
    theme: SelectedTheme,
    modifier: Modifier = Modifier,
    onSelectTheme: (SelectedTheme) -> Unit,
) {
    val nameTheme = stringResource(id = getNameBySelectedTheme(theme))
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.clickableRipple { showMenu = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            modifier = Modifier.weight(weight = 1f),
            titleId = R.string.app_theme_options,
            descriptionId = R.string.app_theme_options_desc,
            titleTextStyle = LocalTypography.current.bodyR14
        )

        Box {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = nameTheme,
                    style = LocalTypography.current.buttonM16
                )

                Icon(
                    painter = painterResource(DesignSystem.drawable.ic_more),
                    contentDescription = nameTheme,
                    tint = LocalPallet.current.iconTint30
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(LocalPallet.current.backgroundDialog)
            ) {
                themes.forEach {
                    DropdownMenuItem(onClick = {
                        showMenu = false
                        onSelectTheme(it)
                    }) {
                        Text(text = stringResource(id = getNameBySelectedTheme(it)))
                    }
                }
            }
        }
    }
}

@Composable
fun getNameBySelectedTheme(theme: SelectedTheme): Int {
    return when (theme) {
        SelectedTheme.DARK -> R.string.app_theme_options_dark
        SelectedTheme.LIGHT -> R.string.app_theme_options_light
        else -> R.string.app_theme_options_system
    }
}
