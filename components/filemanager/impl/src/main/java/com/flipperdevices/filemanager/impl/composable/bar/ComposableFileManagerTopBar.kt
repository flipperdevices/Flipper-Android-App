package com.flipperdevices.filemanager.impl.composable.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFileManagerTopBar(
    path: String,
    onClickUploadButton: () -> Unit,
    onClickAddButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier
            .background(LocalPallet.current.background)
            .statusBarsPadding(),
        backgroundColor = LocalPallet.current.background,
        title = {
            ComposableEllipsizeStartText(
                text = path
            )
        },
        actions = {
            if (isAbleToSave(path)) {
                Row {
                    IconButton(onClick = onClickAddButton) {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_plus
                            ),
                            contentDescription = stringResource(
                                R.string.filemanager_create_action
                            )
                        )
                    }
                    IconButton(onClick = onClickUploadButton) {
                        Icon(
                            painter = painterResource(
                                DesignSystem.drawable.ic_upload
                            ),
                            contentDescription = stringResource(
                                R.string.filemanager_upload_action
                            )
                        )
                    }
                }
            }
        }
    )
}

private fun isAbleToSave(path: String) = path.startsWith("/ext")
