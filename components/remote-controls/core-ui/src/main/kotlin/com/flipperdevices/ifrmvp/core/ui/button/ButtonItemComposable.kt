package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.SquareIconButton
import com.flipperdevices.ifrmvp.core.ui.button.core.TextButton
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.buttondata.Base64ImageButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ChannelButtonData
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData
import com.flipperdevices.ifrmvp.model.buttondata.NavigationButtonData
import com.flipperdevices.ifrmvp.model.buttondata.TextButtonData
import com.flipperdevices.ifrmvp.model.buttondata.UnknownButtonData
import com.flipperdevices.ifrmvp.model.buttondata.VolumeButtonData

@Suppress("LongMethod")
@Composable
fun ButtonItemComposable(
    buttonData: ButtonData,
    emulatedKeyIdentifier: IfrKeyIdentifier?,
    onKeyDataClick: (IfrKeyIdentifier) -> Unit,
    modifier: Modifier = Modifier
) {
    when (buttonData) {
        is IconButtonData -> {
            SquareIconButton(
                iconType = buttonData.iconId,
                modifier = modifier,
                isEmulating = emulatedKeyIdentifier == buttonData.keyIdentifier,
                onClick = { onKeyDataClick.invoke(buttonData.keyIdentifier) }
            )
        }

        is ChannelButtonData -> {
            ChannelButton(
                onNextClick = { onKeyDataClick.invoke(buttonData.addKeyIdentifier) },
                onPrevClick = { onKeyDataClick.invoke(buttonData.reduceKeyIdentifier) },
                modifier = modifier,
                isEmulating = buttonData.reduceKeyIdentifier == emulatedKeyIdentifier ||
                    buttonData.addKeyIdentifier == emulatedKeyIdentifier
            )
        }

        is VolumeButtonData -> {
            VolumeButton(
                onAddClick = { onKeyDataClick.invoke(buttonData.addKeyIdentifier) },
                onReduceClick = { onKeyDataClick.invoke(buttonData.reduceKeyIdentifier) },
                modifier = modifier,
                isEmulating = buttonData.reduceKeyIdentifier == emulatedKeyIdentifier ||
                    buttonData.addKeyIdentifier == emulatedKeyIdentifier
            )
        }

        is NavigationButtonData -> {
            NavigationButton(
                onLeftClick = { onKeyDataClick.invoke(buttonData.leftKeyIdentifier) },
                onRightClick = { onKeyDataClick.invoke(buttonData.rightKeyIdentifier) },
                onDownClick = { onKeyDataClick.invoke(buttonData.downKeyIdentifier) },
                onUpClick = { onKeyDataClick.invoke(buttonData.upKeyIdentifier) },
                onOkClick = { onKeyDataClick.invoke(buttonData.okKeyIdentifier) },
                modifier = modifier,
            )
        }

        is TextButtonData -> {
            TextButton(
                onClick = { onKeyDataClick.invoke(buttonData.keyIdentifier) },
                text = buttonData.text,
                background = LocalPalletV2.current.surface.menu.body.dufault,
                isEmulating = emulatedKeyIdentifier == buttonData.keyIdentifier,
                modifier = modifier,
            )
        }

        is Base64ImageButtonData -> {
            Base64ImageButton(
                base64Icon = buttonData.pngBase64,
                onClick = { onKeyDataClick.invoke(buttonData.keyIdentifier) },
                modifier = modifier,
                isEmulating = emulatedKeyIdentifier == buttonData.keyIdentifier,
            )
        }

        UnknownButtonData -> {
            UnknownButton(
                modifier = modifier,
                onClick = {}
            )
        }
    }
}
