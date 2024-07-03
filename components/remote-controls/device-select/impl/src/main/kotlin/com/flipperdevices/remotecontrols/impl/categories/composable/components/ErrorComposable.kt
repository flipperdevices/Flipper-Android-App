package com.flipperdevices.remotecontrols.impl.categories.composable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.remotecontrols.device.select.impl.R as RemoteSetupR
import androidx.compose.ui.res.stringResource

@Composable
internal fun ErrorComposable(
    desc: String? = null,
    onReload: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(RemoteSetupR.string.error_title),
                    style = MaterialTheme.typography.subtitle2,
                    color = LocalPalletV2.current.text.title.primary
                )

                desc?.let {
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.subtitle2,
                        color = LocalPalletV2.current.text.title.primary
                    )
                }

                onReload?.let {
                    TextButton(onClick = onReload) {
                        Text(
                            text = stringResource(RemoteSetupR.string.error_action),
                            style = MaterialTheme.typography.subtitle2,
                            color = LocalPalletV2.current.action.blue.text.default
                        )
                    }
                }
            }
        }
    )
}
