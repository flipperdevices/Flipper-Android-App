package com.flipperdevices.remotecontrols.impl.setup.composable.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.backend.model.SignalModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponse
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.model.buttondata.TextButtonData
import com.flipperdevices.infrared.api.InfraredConnectionApi
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.setup.impl.R as SetupR

@Composable
fun LoadedContent(
    model: SetupComponent.Model.Loaded,
    onDispatchSignalClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ifrFileModel = model.response.ifrFileModel
    val signalResponse = model.response.signalResponse
    Box(modifier = modifier.fillMaxSize()) {
        when {
            ifrFileModel != null -> Unit

            signalResponse != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PointFlipperComposable()
                    Icon(
                        painter = painterResource(
                            when (MaterialTheme.colors.isLight) {
                                true -> SetupR.drawable.ic_long_arrow_light
                                false -> SetupR.drawable.ic_long_arrow_dark
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(32.dp)
                    )
                    ButtonContent(
                        onClick = onDispatchSignalClick,
                        data = signalResponse.data,
                        emulatedKeyIdentifier = model.emulatedKeyIdentifier,
                        isSyncing = model.isSyncing,
                        isConnected = model.isConnected
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(SetupR.string.rcs_skip_this_button),
                        style = LocalTypography.current.buttonB16,
                        color = LocalPalletV2.current.action.blue.text.default,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickableRipple(onClick = onSkipClick)
                    )
                }
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
                response = SignalResponseModel(
                    signalResponse = SignalResponse(
                        signalModel = SignalModel(
                            id = -1,
                            remote = SignalModel.FlipperRemote(
                                name = "name",
                                type = "type"
                            ),
                        ),
                        message = "message",
                        categoryName = "category_name",
                        data = TextButtonData(text = "Hello")
                    )
                ),
                emulatedKeyIdentifier = null,
                connectionState = InfraredConnectionApi.InfraredEmulateState.ALL_GOOD
            ),
            onDispatchSignalClick = {},
            onSkipClick = {}
        )
    }
}
