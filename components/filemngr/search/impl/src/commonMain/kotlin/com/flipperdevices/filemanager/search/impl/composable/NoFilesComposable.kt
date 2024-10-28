package com.flipperdevices.filemanager.search.impl.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import flipperapp.components.filemngr.search.impl.generated.resources.fms_no_files
import flipperapp.components.filemngr.ui_components.generated.resources.ic__no_files_black
import flipperapp.components.filemngr.ui_components.generated.resources.ic__no_files_white
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.search.impl.generated.resources.Res as FMS
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Composable
fun NoFilesComposable(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(FMS.string.fms_no_files),
                style = LocalTypography.current.titleB18,
                color = LocalPalletV2.current.text.title.primary
            )
            Image(
                painter = painterResource(
                    when {
                        MaterialTheme.colors.isLight -> FR.drawable.ic__no_files_white
                        else -> FR.drawable.ic__no_files_black
                    }
                ),
                contentDescription = null,
                modifier = Modifier.height(100.dp)
            )
        }
    }
}
