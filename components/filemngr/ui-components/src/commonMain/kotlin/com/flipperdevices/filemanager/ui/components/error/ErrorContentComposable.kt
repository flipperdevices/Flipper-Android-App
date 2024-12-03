package com.flipperdevices.filemanager.ui.components.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import flipperapp.components.filemngr.ui_components.generated.resources.Res
import flipperapp.components.filemngr.ui_components.generated.resources.filemngr_error_retry
import flipperapp.components.filemngr.ui_components.generated.resources.ic__no_files_black
import flipperapp.components.filemngr.ui_components.generated.resources.ic__no_files_white
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorContentComposable(
    text: String,
    desc: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    painter: Painter = painterResource(
        when {
            MaterialTheme.colors.isLight -> Res.drawable.ic__no_files_white
            else -> Res.drawable.ic__no_files_black
        }
    ),
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Image(
            modifier = Modifier.size(124.dp),
            painter = painter,
            contentDescription = null
        )
        Text(
            text = text,
            style = LocalTypography.current.bodyM14,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = desc,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyR14.copy(
                color = LocalPallet.current.text30
            )
        )
        if (onRetry != null) {
            Text(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickableRipple(onClick = onRetry),
                text = stringResource(Res.string.filemngr_error_retry),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.buttonM16.copy(
                    color = LocalPallet.current.accentSecond
                )
            )
        }
    }
}
