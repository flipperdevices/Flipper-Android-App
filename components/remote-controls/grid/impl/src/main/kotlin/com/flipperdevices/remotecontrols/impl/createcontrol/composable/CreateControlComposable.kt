package com.flipperdevices.remotecontrols.impl.createcontrol.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun CreateControlComposable() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Syncing",
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
            text = "Configuring remote control",
            color = LocalPalletV2.current.text.body.secondary,
            style = LocalTypography.current.subtitleM12
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