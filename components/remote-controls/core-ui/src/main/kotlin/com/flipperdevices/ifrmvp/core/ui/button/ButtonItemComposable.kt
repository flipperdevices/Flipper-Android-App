package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.ButtonPlaceholderComposition
import com.flipperdevices.ifrmvp.core.ui.button.core.SquareIconButton
import com.flipperdevices.ifrmvp.core.ui.button.core.TextButton
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.buttondata.Base64ImageButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ChannelButtonData
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData
import com.flipperdevices.ifrmvp.model.buttondata.NavigationButtonData
import com.flipperdevices.ifrmvp.model.buttondata.OkNavigationButtonData
import com.flipperdevices.ifrmvp.model.buttondata.PowerButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ShutterButtonData
import com.flipperdevices.ifrmvp.model.buttondata.TextButtonData
import com.flipperdevices.ifrmvp.model.buttondata.UnknownButtonData
import com.flipperdevices.ifrmvp.model.buttondata.VolumeButtonData

@Suppress("LongMethod")
@Composable
fun ButtonItemComposable(
    buttonData: ButtonData,
    emulatedKeyIdentifier: IfrKeyIdentifier?,
    onKeyDataClick: (IfrKeyIdentifier) -> Unit,
    isSyncing: Boolean,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    when (buttonData) {
        is IconButtonData -> {
            ButtonPlaceholderComposition(
                isSyncing = isSyncing,
                isConnected = isConnected,
                isEmulating = emulatedKeyIdentifier == buttonData.keyIdentifier,
                content = {
                    SquareIconButton(
                        iconType = buttonData.iconId,
                        modifier = modifier,
                        onClick = { onKeyDataClick.invoke(buttonData.keyIdentifier) },
                    )
                }
            )
        }

        is ChannelButtonData -> {
            ButtonPlaceholderComposition(
                isSyncing = isSyncing,
                isConnected = isConnected,
                isEmulating = buttonData.reduceKeyIdentifier == emulatedKeyIdentifier ||
                    buttonData.addKeyIdentifier == emulatedKeyIdentifier,
                content = {
                    ChannelButton(
                        onNextClick = { onKeyDataClick.invoke(buttonData.addKeyIdentifier) },
                        onPrevClick = { onKeyDataClick.invoke(buttonData.reduceKeyIdentifier) },
                        modifier = modifier,
                    )
                }
            )
        }

        is VolumeButtonData -> {
            ButtonPlaceholderComposition(
                isSyncing = isSyncing,
                isConnected = isConnected,
                isEmulating = buttonData.reduceKeyIdentifier == emulatedKeyIdentifier ||
                    buttonData.addKeyIdentifier == emulatedKeyIdentifier,
                content = {
                    VolumeButton(
                        onAddClick = { onKeyDataClick.invoke(buttonData.addKeyIdentifier) },
                        onReduceClick = { onKeyDataClick.invoke(buttonData.reduceKeyIdentifier) },
                        modifier = modifier,
                    )
                }
            )
        }

        is OkNavigationButtonData -> {
            ButtonPlaceholderComposition(
                isSyncing = isSyncing,
                isConnected = isConnected,
                isEmulating = listOf(
                    buttonData.okKeyIdentifier,
                    buttonData.upKeyIdentifier,
                    buttonData.rightKeyIdentifier,
                    buttonData.downKeyIdentifier,
                    buttonData.leftKeyIdentifier
                ).contains(emulatedKeyIdentifier),
                content = {
                    NavigationButton(
                        onLeftClick = { onKeyDataClick.invoke(buttonData.leftKeyIdentifier) },
                        onRightClick = { onKeyDataClick.invoke(buttonData.rightKeyIdentifier) },
                        onDownClick = { onKeyDataClick.invoke(buttonData.downKeyIdentifier) },
                        onUpClick = { onKeyDataClick.invoke(buttonData.upKeyIdentifier) },
                        onOkClick = { onKeyDataClick.invoke(buttonData.okKeyIdentifier) },
                        modifier = modifier,
                    )
                }
            )
        }

        is NavigationButtonData -> {
            ButtonPlaceholderComposition(
                isConnected = isConnected,
                isSyncing = isSyncing,
                isEmulating = listOf(
                    buttonData.upKeyIdentifier,
                    buttonData.rightKeyIdentifier,
                    buttonData.downKeyIdentifier,
                    buttonData.leftKeyIdentifier
                ).contains(emulatedKeyIdentifier),
                content = {
                    NavigationButton(
                        onLeftClick = { onKeyDataClick.invoke(buttonData.leftKeyIdentifier) },
                        onRightClick = { onKeyDataClick.invoke(buttonData.rightKeyIdentifier) },
                        onDownClick = { onKeyDataClick.invoke(buttonData.downKeyIdentifier) },
                        onUpClick = { onKeyDataClick.invoke(buttonData.upKeyIdentifier) },
                        onOkClick = null,
                        modifier = modifier,
                    )
                }
            )
        }

        is TextButtonData -> {
            ButtonPlaceholderComposition(
                isSyncing = isSyncing,
                isConnected = isConnected,
                isEmulating = emulatedKeyIdentifier == buttonData.keyIdentifier,
                content = {
                    TextButton(
                        onClick = { onKeyDataClick.invoke(buttonData.keyIdentifier) },
                        text = buttonData.text,
                        background = LocalPalletV2.current.surface.menu.body.dufault,
                        modifier = modifier,
                    )
                }
            )
        }

        is Base64ImageButtonData -> {
            ButtonPlaceholderComposition(
                isSyncing = isSyncing,
                isConnected = isConnected,
                isEmulating = emulatedKeyIdentifier == buttonData.keyIdentifier,
                content = {
                    Base64ImageButton(
                        base64Icon = buttonData.pngBase64,
                        onClick = { onKeyDataClick.invoke(buttonData.keyIdentifier) },
                        modifier = modifier,
                    )
                }
            )
        }

        UnknownButtonData -> {
            ButtonPlaceholderComposition(
                isSyncing = isSyncing,
                isConnected = isConnected,
                isEmulating = false,
                content = {
                    UnknownButton(
                        modifier = modifier,
                        onClick = {},
                    )
                }
            )
        }

        is PowerButtonData -> {
            ButtonPlaceholderComposition(
                isConnected = isConnected,
                isSyncing = isSyncing,
                isEmulating = emulatedKeyIdentifier == buttonData.keyIdentifier,
                content = {
                    SquareIconButton(
                        iconType = IconButtonData.IconType.POWER,
                        modifier = modifier,
                        onClick = { onKeyDataClick.invoke(buttonData.keyIdentifier) },
                        background = LocalPalletV2.current.icon.danger.primary,
                        iconTint = MaterialTheme.colors.onPrimary
                    )
                }
            )
        }

        is ShutterButtonData -> {
            ShutterButtonComposable(
                onClick = { onKeyDataClick.invoke(buttonData.keyIdentifier) }
            )
        }
    }
}

@Preview
@Composable
private fun ButtonItemComposablePreview() {
    FlipperThemeInternal {
        ButtonItemComposable(
            buttonData = TextButtonData(text = "TXT"),
            emulatedKeyIdentifier = null,
            onKeyDataClick = {},
            isSyncing = false,
            isConnected = false
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.sf)) {
            IconButtonData.IconType.entries.chunked(size = 6).forEach { entries ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.sf)) {
                    entries.forEach { iconType ->
                        ButtonItemComposable(
                            buttonData = IconButtonData(iconId = iconType),
                            emulatedKeyIdentifier = null,
                            onKeyDataClick = {},
                            isSyncing = false,
                            isConnected = false
                        )
                    }
                }
            }
        }
    }
}
