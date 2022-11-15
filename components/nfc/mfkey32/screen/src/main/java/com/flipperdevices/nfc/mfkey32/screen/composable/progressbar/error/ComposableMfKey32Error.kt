package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.error

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.model.ErrorType
import com.flipperdevices.nfc.mfkey32.screen.viewmodel.FlipperColorViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableMfKey32Error(errorType: ErrorType) {
    when (errorType) {
        ErrorType.NOT_FOUND_FILE -> ComposableMfKey32NotFound()
        ErrorType.READ_WRITE -> ComposableMfKey32ReadWrite()
        ErrorType.FLIPPER_CONNECTION -> ComposableMfKey32ConnectFailed()
    }
}

@Composable
fun ComposableMfKey32ErrorContent(
    @StringRes titleId: Int,
    @DrawableRes picId: Int,
    @DrawableRes picIdBlack: Int,
    content: @Composable () -> Unit
) = Column(
    modifier = Modifier.fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        modifier = Modifier.padding(top = 32.dp, bottom = 18.dp),
        text = stringResource(titleId),
        style = LocalTypography.current.titleSB18,
        color = LocalPallet.current.text100,
        textAlign = TextAlign.Center
    )
    val flipperColorViewModel = tangleViewModel<FlipperColorViewModel>()
    val flipperColor by flipperColorViewModel.getFlipperColor().collectAsState()
    Image(
        modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 32.dp),
        painter = painterResource(
            when (flipperColor) {
                HardwareColor.WHITE,
                HardwareColor.UNRECOGNIZED -> picId
                HardwareColor.BLACK -> picIdBlack
            }
        ),
        contentDescription = stringResource(titleId)
    )

    content()
}
