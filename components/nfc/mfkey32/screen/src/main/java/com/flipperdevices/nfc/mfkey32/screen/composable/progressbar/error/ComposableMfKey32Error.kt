package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.error

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.flippermockup.ComposableFlipperMockup
import com.flipperdevices.core.ui.flippermockup.ComposableFlipperMockupImage
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
    mockupImage: ComposableFlipperMockupImage,
    isActive: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier.fillMaxHeight(),
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

    ComposableFlipperMockup(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 32.dp),
        flipperColor = flipperColor,
        isActive = isActive,
        mockupImage = mockupImage
    )

    content()
}
