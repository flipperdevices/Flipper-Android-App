package com.flipperdevices.remotecontrols.impl.setup.composable.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.setup.impl.R as SetupR

@Composable
fun LoadedContent(
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
            ifrFileModel != null -> Unit

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
                response = SignalResponseModel()
            ),
            onPositiveClicked = {},
            onNegativeClicked = {},
            onDispatchSignalClicked = {}
        )
    }
}
