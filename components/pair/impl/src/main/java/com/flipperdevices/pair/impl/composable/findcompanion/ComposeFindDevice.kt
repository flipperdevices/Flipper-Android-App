package com.flipperdevices.pair.impl.composable.findcompanion

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flipperdevices.pair.impl.R
import com.flipperdevices.pair.impl.composable.common.ComposableBackButton
import com.flipperdevices.pair.impl.composable.common.ComposePairScreen
import com.flipperdevices.pair.impl.model.findcompanion.PairingState
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
    Scaffold(
        bottomBar = {
            ComposeBottomBar(
                onClickBackButton,
                onClickRefreshButton,
                pairingState
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) { PairingState(pairingState) }
    }
}

@Composable
private fun ComposeBottomBar(
    onClickBackButton: () -> Unit,
    onClickRefreshButton: () -> Unit,
    pairingState: PairingState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableBackButton(
            text = stringResource(R.string.pair_companion_skip_pairing),
            onPressListener = onClickBackButton
        )

        IconButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onClickRefreshButton
        ) {
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

@Composable
private fun PairingState(pairingState: PairingState) {
    when (pairingState) {
        PairingState.NotInitialized -> ComposePairScreen(
            title = stringResource(R.string.pair_companion_title_find),
            description = stringResource(R.string.pair_companion_desc_not_start_yet)
        ) {
            ComposePairPic(
                picResId = R.drawable.ic_find,
                picDesc = R.string.pair_companion_desc_not_start_yet
            )
        }
        is PairingState.Failed -> ComposePairScreen(
            title = stringResource(R.string.pair_companion_title_failed),
            description = pairingState.reason
        ) {
            ComposePairPic(
                picResId = R.drawable.ic_error_colored,
                picDesc = R.string.pair_companion_desc_failed
            )
        }
        PairingState.FindingDevice -> ComposePairScreen(
            title = stringResource(R.string.pair_companion_title_find),
            description = stringResource(R.string.pair_companion_desc_finding)
        ) {
            ComposePairPic(
                picResId = R.drawable.ic_find,
                picDesc = R.string.pair_companion_desc_finding
            )
        }

        is PairingState.WithDevice -> ComposeProcessingConnectionState(pairingState.connectionState)
    }
}

@Composable
private fun ComposeProcessingConnectionState(connectionState: ConnectionState) {
    when (connectionState) {
        ConnectionState.Connecting -> ComposePairScreen(
            title = stringResource(R.string.pair_companion_title_connecting),
            description = stringResource(R.string.pair_companion_desc_initializing)
        ) {
            ComposeLottiePic(
                picResId = R.raw.ic_connecting,
                rollBackPicResId = R.drawable.ic_connecting_frame1
            )
        }
        ConnectionState.Initializing -> ComposePairScreen(
            title = stringResource(R.string.pair_companion_title_connecting),
            description = stringResource(R.string.pair_companion_desc_initializing)
        ) {
            ComposeLottiePic(
                picResId = R.raw.ic_connecting,
                rollBackPicResId = R.drawable.ic_connecting_frame1
            )
        }
        ConnectionState.Ready -> ComposePairScreen(
            title = stringResource(R.string.pair_companion_title_connecting),
            description = stringResource(R.string.pair_companion_desc_done)
        ) {
            ComposePairPic(
                picResId = R.drawable.ic_done,
                picDesc = R.string.pair_companion_desc_done
            )
        }
        ConnectionState.Disconnecting, is ConnectionState.Disconnected -> ComposePairScreen(
            title = stringResource(R.string.pair_companion_title_connecting),
            description = stringResource(R.string.pair_companion_desc_disconnect)
        ) {
            ComposePairPic(
                picResId = R.drawable.ic_error_colored,
                picDesc = R.string.pair_companion_desc_disconnect
            )
        }
    }
}

@Composable
private fun ComposePairPic(
    @DrawableRes picResId: Int,
    @StringRes picDesc: Int
) {
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(picResId),
        contentDescription = stringResource(picDesc)
    )
}

@Composable
private fun ComposeLottiePic(
    @RawRes picResId: Int,
    @DrawableRes rollBackPicResId: Int
) {
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(picResId))
    val composition by compositionResult
    val animateState = animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val progress by animateState

    Box {
        if (compositionResult.isLoading) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(rollBackPicResId),
                contentDescription = null
            )
        } else {
            LottieAnimation(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                composition = composition,
                progress = progress
            )
        }
    }
}
