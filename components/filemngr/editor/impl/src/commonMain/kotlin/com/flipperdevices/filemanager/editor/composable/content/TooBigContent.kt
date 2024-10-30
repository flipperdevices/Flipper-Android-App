package com.flipperdevices.filemanager.editor.composable.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPallet
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_too_large_file
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.editor.impl.generated.resources.Res as FME

@Composable
fun TooBigContent(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalPallet.current.warningColor),
        text = stringResource(FME.string.fme_too_large_file),
        color = LocalPallet.current.textOnWarningBackground
    )
}
