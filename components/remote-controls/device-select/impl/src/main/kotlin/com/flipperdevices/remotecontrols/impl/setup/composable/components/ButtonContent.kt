package com.flipperdevices.remotecontrols.impl.setup.composable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.backend.model.SignalResponse
import com.flipperdevices.remotecontrols.device.select.impl.R as RemoteSetupR
import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.backend.model.SignalModel
import com.flipperdevices.ifrmvp.core.ui.button.UnknownButton
import com.flipperdevices.ifrmvp.core.ui.button.core.SquareIconButton
import com.flipperdevices.ifrmvp.core.ui.button.core.TextButton
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData

@Composable
private fun SignalResponseButton(
    data: SignalResponse.Data,
    onClick: () -> Unit
) {
    val text = data.text
    val iconType = IconButtonData.IconType.entries.firstOrNull { it.name == data.iconId }
    when {
        text != null -> {
            TextButton(
                text = text,
                onClick = onClick,
                modifier = Modifier.size(64.dp)
            )
        }

        iconType != null -> {
            SquareIconButton(
                iconType = iconType,
                onClick = onClick,
                modifier = Modifier.size(64.dp)
            )
        }

        else -> {
            UnknownButton(
                onClick = onClick,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
internal fun ButtonContent(
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
    data: SignalResponse.Data,
    categoryName: String
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignalResponseButton(data = data, onClick = onClicked)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(RemoteSetupR.string.point_flipper)
                .format(categoryName),
            style = LocalTypography.current.bodyM14,
            color = LocalPalletV2.current.text.body.secondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ComposableConfirmContentDarkPreview() {
    FlipperThemeInternal {
        Column {
            ButtonContent(
                onClicked = {},
                categoryName = "CATEGORY",
                data = SignalResponse.Data(
                    type = "ANY",
                    iconId = "HOME"
                )
            )
            ButtonContent(
                onClicked = {},
                categoryName = "CATEGORY 2",
                data = SignalResponse.Data(
                    type = "ANY",
                    text = "TV/AV"
                )
            )
            ButtonContent(
                onClicked = {},
                categoryName = "CATEGORY 2",
                data = SignalResponse.Data(
                    type = "ANY",
                )
            )
        }
    }
}