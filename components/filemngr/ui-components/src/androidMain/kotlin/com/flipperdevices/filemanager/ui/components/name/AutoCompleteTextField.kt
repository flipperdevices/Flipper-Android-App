package com.flipperdevices.filemanager.ui.components.name

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun AutoCompleteTextField(
    value: String,
    title: String,
    subtitle: String,
    isError: Boolean,
    onTextChange: (String) -> Unit,
    options: ImmutableList<String>,
    onOptionSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    needShowOptions: Boolean = true,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        FlipperTextField(
            title = title,
            subtitle = subtitle,
            value = value,
            onTextChange = onTextChange,
            interactionSource = interactionSource,
            modifier = Modifier.onFocusEvent { isExpanded = it.isFocused },
            isError = isError
        )

        DropdownMenu(
            modifier = Modifier
                .heightIn(max = 152.dp)
                .wrapContentWidth(),
            expanded = isExpanded && needShowOptions && options.isNotEmpty(),
            onDismissRequest = { },
            properties = PopupProperties(focusable = false)
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    modifier = Modifier,
                    onClick = { onOptionSelect.invoke(index) },
                    content = {
                        Text(
                            text = option,
                            color = LocalPalletV2.current.text.body.primary,
                            style = LocalTypography.current.bodyM14
                        )
                    }
                )
                if (index < options.lastIndex) {
                    Divider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}
