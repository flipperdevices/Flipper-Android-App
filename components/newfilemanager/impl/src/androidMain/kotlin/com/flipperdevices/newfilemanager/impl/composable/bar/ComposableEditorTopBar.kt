package com.flipperdevices.newfilemanager.impl.composable.bar

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.flipperdevices.newfilemanager.impl.R
import flipperapp.components.newfilemanager.impl.generated.resources.Res
import flipperapp.components.newfilemanager.impl.generated.resources.filemanager_save_action
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComposableEditorTopBar(
    path: String,
    onClickSaveButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.statusBarsPadding(),
        title = {
            ComposableEllipsizeStartText(
                text = path
            )
        },
        actions = {
            IconButton(onClick = onClickSaveButton) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_ok
                    ),
                    contentDescription = stringResource(
                        Res.string.filemanager_save_action
                    )
                )
            }
        }
    )
}
