package com.flipper.pair.impl.findcompanion.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipper.pair.impl.R
import com.flipper.pair.impl.model.findcompanion.PairingState
import no.nordicsemi.android.ble.ktx.state.ConnectionState

@Preview(
    showBackground = true,
    showSystemUi = false
)
@Composable
fun ComposeFindDevice(
    pairingState: PairingState = PairingState.NotInitialized,
    onClickBackButton: () -> Unit = {},
    onClickRefreshButton: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(all = 16.dp),
            contentAlignment = Alignment.Center
        ) { PairingState(pairingState) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onClickBackButton) {
                Text(stringResource(R.string.pair_companion_back_button))
            }

            IconButton(onClick = onClickRefreshButton) {
                Icon(
                    painter = painterResource(
                        if (pairingState is PairingState.Failed) {
                            R.drawable.ic_sync_problem
                        } else {
                            R.drawable.ic_sync
                        }
                    ),
                    contentDescription = stringResource(R.string.pair_companion_pic_update)
                )
            }
        }
    }
}

@Composable
private fun PairingState(pairingState: PairingState) {
    when (pairingState) {
        PairingState.NotInitialized -> ComposeConnectionState(
            pic = R.drawable.ic_sync,
            picDesc = R.string.pair_companion_desc_not_start_yet,
            text = stringResource(R.string.pair_companion_desc_not_start_yet)
        )
        is PairingState.Failed -> ComposeConnectionState(
            pic = R.drawable.ic_warning,
            picDesc = R.string.pair_companion_desc_failed,
            text = pairingState.reason
        )
        PairingState.FindingDevice -> ComposeConnectionState(
            pic = R.drawable.ic_sync,
            picDesc = R.string.pair_companion_desc_finding,
            text = stringResource(R.string.pair_companion_desc_finding)
        )

        is PairingState.WithDevice -> ComposeProcessingConnectionState(pairingState.connectionState)
    }
}

@Composable
private fun ComposeProcessingConnectionState(connectionState: ConnectionState) {
    when (connectionState) {
        ConnectionState.Connecting -> ComposeConnectionState(
            pic = R.drawable.ic_sync,
            picDesc = R.string.pair_companion_desc_connecting,
            text = stringResource(R.string.pair_companion_desc_connecting)
        )
        ConnectionState.Initializing -> ComposeConnectionState(
            pic = R.drawable.ic_sync,
            picDesc = R.string.pair_companion_desc_initializing,
            text = stringResource(R.string.pair_companion_desc_initializing)
        )
        ConnectionState.Ready -> ComposeConnectionState(
            pic = R.drawable.ic_done,
            picDesc = R.string.pair_companion_desc_done,
            text = stringResource(R.string.pair_companion_desc_done)
        )
        ConnectionState.Disconnecting, is ConnectionState.Disconnected -> ComposeConnectionState(
            pic = R.drawable.ic_warning,
            picDesc = R.string.pair_companion_desc_disconnect,
            text = stringResource(R.string.pair_companion_desc_disconnect)
        )
    }
}

@Composable
private fun ComposeConnectionState(
    @DrawableRes pic: Int,
    @StringRes picDesc: Int,
    text: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier
                .height(256.dp)
                .width(256.dp),
            painter = painterResource(pic),
            contentDescription = stringResource(picDesc)
        )
        Text(text = text, textAlign = TextAlign.Center)
    }
}
