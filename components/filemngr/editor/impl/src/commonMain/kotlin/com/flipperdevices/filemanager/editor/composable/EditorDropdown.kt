package com.flipperdevices.filemanager.editor.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.ui.components.dropdown.TextDropdownItem
import flipperapp.components.core.ui.res.generated.resources.Res
import flipperapp.components.core.ui.res.generated.resources.ic_more_points
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_save
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_save_as_file
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.editor.impl.generated.resources.Res as FME

@Composable
internal fun EditorDropdown(
    onSaveClick: () -> Unit,
    onSaveAsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDropdownVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Box(modifier = modifier) {
        Icon(
            modifier = Modifier
                .padding(end = 14.dp)
                .size(24.dp)
                .clickableRipple(onClick = { isDropdownVisible = !isDropdownVisible }),
            painter = painterResource(Res.drawable.ic_more_points),
            contentDescription = null,
            tint = LocalPalletV2.current.icon.blackAndWhite.blackOnColor
        )
        DropdownMenu(
            modifier = Modifier,
            expanded = isDropdownVisible,
            onDismissRequest = { isDropdownVisible = !isDropdownVisible },
        ) {
            TextDropdownItem(
                text = stringResource(FME.string.fme_save),
                onClick = {
                    onSaveClick.invoke()
                    isDropdownVisible = !isDropdownVisible
                }
            )
            TextDropdownItem(
                text = stringResource(FME.string.fme_save_as_file),
                onClick = {
                    onSaveAsClick.invoke()
                    isDropdownVisible = !isDropdownVisible
                }
            )
        }
    }
}
