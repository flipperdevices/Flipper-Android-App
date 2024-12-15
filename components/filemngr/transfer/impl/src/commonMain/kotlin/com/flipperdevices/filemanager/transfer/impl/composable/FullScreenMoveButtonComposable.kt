package com.flipperdevices.filemanager.transfer.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import flipperapp.components.filemngr.transfer.impl.generated.resources.Res
import flipperapp.components.filemngr.transfer.impl.generated.resources.fmt_move_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun FullScreenMoveButtonComposable(
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
        content = {
            ComposableFlipperButton(
                text = stringResource(Res.string.fmt_move_button),
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                enabled = isEnabled,
                onClick = onClick
            )
        }
    )
}
