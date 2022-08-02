package com.flipperdevices.filemanager.impl.composable.bar

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.filemanager.impl.R

@Composable
fun ComposableFileManagerTopBar(path: String, onClickUploadButton: () -> Unit) {
    TopAppBar(
        title = {
            ComposableEllipsizeStartText(
                text = path
            )
        },
        actions = {
            if (isAbleToSafe(path)) {
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
    )
}

private fun isAbleToSafe(path: String) = path.startsWith("/ext")
