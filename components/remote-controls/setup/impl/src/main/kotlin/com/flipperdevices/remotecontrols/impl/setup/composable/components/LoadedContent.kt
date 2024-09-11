package com.flipperdevices.remotecontrols.impl.setup.composable.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.infrared.api.InfraredConnectionApi
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.setup.impl.R as SetupR

@Composable
fun LoadedContent(
    model: SetupComponent.Model.Loaded,
    onDispatchSignalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ifrFileModel = model.response.ifrFileModel
    val signalResponse = model.response.signalResponse
    Box(modifier = modifier.fillMaxSize()) {
        when {
            ifrFileModel != null -> Unit

            signalResponse != null -> {
                ButtonContent(
                    onClick = onDispatchSignalClick,
                    modifier = Modifier.align(Alignment.Center),
                    data = signalResponse.data,
                    emulatedKeyIdentifier = model.emulatedKeyIdentifier,
                    isSyncing = model.isSyncing,
                    isConnected = model.isConnected
                )
            }

            else -> {
                ErrorComposable(
                    desc = stringResource(SetupR.string.not_found_signal),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LoadedContentPreview() {
    FlipperThemeInternal {
        LoadedContent(
            model = SetupComponent.Model.Loaded(
                response = SignalResponseModel(),
                emulatedKeyIdentifier = null,
                connectionState = InfraredConnectionApi.InfraredEmulateState.ALL_GOOD
            ),
            onDispatchSignalClick = {}
        )
    }
}
