package com.flipperdevices.remotecontrols.impl.setup.composable.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.core.ui.button.ButtonItemComposable
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.buttondata.ButtonData
import com.flipperdevices.ifrmvp.model.buttondata.TextButtonData
import com.flipperdevices.remotecontrols.setup.impl.R as SetupR

@Composable
private fun SignalResponseButton(
    data: ButtonData,
    onClick: () -> Unit,
    emulatedKeyIdentifier: IfrKeyIdentifier?,
    isSyncing: Boolean,
    isConnected: Boolean
) {
    ButtonItemComposable(
        buttonData = data,
        onKeyDataClick = { onClick.invoke() },
        modifier = Modifier.size(64.dp),
        emulatedKeyIdentifier = emulatedKeyIdentifier,
        isSyncing = isSyncing,
        isConnected = isConnected,
    )
}

@Composable
fun ButtonContent(
    onClick: () -> Unit,
    data: ButtonData,
    isSyncing: Boolean,
    isConnected: Boolean,
    emulatedKeyIdentifier: IfrKeyIdentifier?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignalResponseButton(
            data = data,
            onClick = onClick,
            emulatedKeyIdentifier = emulatedKeyIdentifier,
            isSyncing = isSyncing,
            isConnected = isConnected,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(SetupR.string.point_flipper),
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
                onClick = {},
                data = TextButtonData(text = "Hello"),
                emulatedKeyIdentifier = null,
                isSyncing = false,
                isConnected = true
            )
            ButtonContent(
                onClick = {},
                data = TextButtonData(text = "TV/AV"),
                emulatedKeyIdentifier = null,
                isSyncing = false,
                isConnected = true
            )
            ButtonContent(
                onClick = {},
                data = TextButtonData(text = "Hello world"),
                emulatedKeyIdentifier = null,
                isSyncing = false,
                isConnected = true
            )
        }
    }
}
