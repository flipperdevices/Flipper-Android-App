package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun ButtonItemComposable(
    buttonData: ButtonData,
    onKeyDataClicked: (IfrKeyIdentifier) -> Unit,
    modifier: Modifier = Modifier
) {
    when (buttonData) {
        is IconButtonData -> {
            SquareIconButton(
                iconType = buttonData.iconId,
                modifier = modifier,
                onClick = { onKeyDataClicked.invoke(buttonData.keyIdentifier) }
            )
        }

        is ChannelButtonData -> {
            ChannelButton(
                onNextClicked = { onKeyDataClicked.invoke(buttonData.addKeyIdentifier) },
                onPrevClicked = { onKeyDataClicked.invoke(buttonData.reduceKeyIdentifier) },
                modifier = modifier,
            )
        }

        is VolumeButtonData -> {
            VolumeButton(
                onAddClicked = { onKeyDataClicked.invoke(buttonData.addKeyIdentifier) },
                onReduceClicked = { onKeyDataClicked.invoke(buttonData.reduceKeyIdentifier) },
                modifier = modifier,
            )
        }

        is NavigationButtonData -> {
            NavigationButton(
                onLeftClicked = { onKeyDataClicked.invoke(buttonData.leftKeyIdentifier) },
                onRightClicked = { onKeyDataClicked.invoke(buttonData.rightKeyIdentifier) },
                onDownClicked = { onKeyDataClicked.invoke(buttonData.downKeyIdentifier) },
                onUpClicked = { onKeyDataClicked.invoke(buttonData.upKeyIdentifier) },
                onOkClicked = { onKeyDataClicked.invoke(buttonData.okKeyIdentifier) },
                modifier = modifier,
            )
        }

        is TextButtonData -> {
            TextButton(
                onClick = { onKeyDataClicked.invoke(buttonData.keyIdentifier) },
                text = buttonData.text,
                background = Color(0xFF303030),
                modifier = modifier,
            )
        }

        is Base64ImageButtonData -> {
            Base64ImageButton(
                base64Icon = buttonData.pngBase64,
                onClick = { onKeyDataClicked.invoke(buttonData.keyIdentifier) },
                modifier = modifier,
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
