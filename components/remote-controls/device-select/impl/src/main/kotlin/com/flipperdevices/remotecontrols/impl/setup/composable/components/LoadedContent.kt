package com.flipperdevices.remotecontrols.impl.setup.composable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.remotecontrols.device.select.impl.R as RemoteSetupR
import android.content.res.Configuration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.remotecontrols.impl.categories.composable.components.ErrorComposable
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent

@Composable
internal fun LoadedContent(
    model: SetupComponent.Model.Loaded,
    onPositiveClicked: () -> Unit,
    onNegativeClicked: () -> Unit,
    onDispatchSignalClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ifrFileModel = model.response.ifrFileModel
    val signalResponse = model.response.signalResponse
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when {
            ifrFileModel != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            text = "Yappie! Found your remote!",
                            style = MaterialTheme.typography.subtitle2,
                            color = LocalPalletV2.current.text.title.primary
                        )
                    }
                )
            }

            signalResponse != null -> {
                Box(modifier = Modifier)
                ButtonContent(
                    onClicked = onDispatchSignalClicked,
                    modifier = Modifier,
                    data = signalResponse.data,
                    categoryName = signalResponse.categoryName
                )
                ConfirmContent(
                    text = signalResponse.message,
                    onNegativeClicked = onNegativeClicked,
                    onPositiveClicked = onPositiveClicked,
                    modifier = Modifier
                )
            }

            else -> {
                ErrorComposable(
                    desc = stringResource(RemoteSetupR.string.not_found_signal),
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
                response = SignalResponseModel()
            ),
            onPositiveClicked = {},
            onNegativeClicked = {},
            onDispatchSignalClicked = {}
        )
    }
}