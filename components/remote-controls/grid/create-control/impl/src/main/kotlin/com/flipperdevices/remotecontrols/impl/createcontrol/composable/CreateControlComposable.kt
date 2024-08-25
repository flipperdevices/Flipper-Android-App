package com.flipperdevices.remotecontrols.impl.createcontrol.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.remotecontrols.grid.createcontrol.impl.R

@Composable
internal fun CreateControlComposable() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.configuring_title),
            color = LocalPalletV2.current.text.body.primary,
            style = LocalTypography.current.titleB18
        )
        Spacer(Modifier.height(24.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = LocalPallet.current.accentSecond
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.configuring_desc),
            color = LocalPalletV2.current.text.body.secondary,
            style = LocalTypography.current.subtitleM12,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun CreateControlComposablePreview() {
    FlipperThemeInternal {
        CreateControlComposable()
    }
}
