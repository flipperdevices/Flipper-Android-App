package com.flipperdevices.remotecontrols.impl.grid.remote.composable.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.grid.remote.impl.R

@Composable
internal fun RemoteGridTopBar(
    isFilesSaved: Boolean,
    remoteName: String?,
    saveProgress: Int?,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    SharedTopBar(
        onBackClick = onBack,
        title = remoteName.orEmpty(),
        background = LocalPalletV2.current.surface.navBar.body.main,
        backIconTint = LocalPalletV2.current.icon.blackAndWhite.default,
        textColor = LocalPalletV2.current.text.title.primary,
        subtitle = when {
            saveProgress == null -> {
                stringResource(R.string.remote_subtitle)
            }

            else -> {
                val progress by animateIntAsState(saveProgress)
                stringResource(R.string.uploading_to_flipper).format("$progress%")
            }
        },
        actions = {
            AnimatedVisibility(
                visible = isFilesSaved,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(modifier = Modifier) {
                    Text(
                        text = stringResource(R.string.save),
                        color = LocalPalletV2.current.text.title.primary,
                        style = LocalTypography.current.titleEB18,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickableRipple(onClick = onSave)
                    )
                }
            }
        }
    )
}
