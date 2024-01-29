package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.flippermockup.ComposableFlipperMockup
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R

@Composable
fun ComposableWaitingFlipperConnection(
    flipperColor: HardwareColor,
    modifier: Modifier = Modifier
) {
    ComposableWaitingFlipperConnectionInternal(
        flipperColor = flipperColor,
        modifier = modifier
    )
}

@Composable
private fun ComposableWaitingFlipperConnectionInternal(
    flipperColor: HardwareColor,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier.fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        modifier = Modifier.padding(top = 32.dp, bottom = 18.dp),
        text = stringResource(R.string.mfkey32_connecting),
        style = LocalTypography.current.titleSB18,
        color = LocalPallet.current.text100,
        textAlign = TextAlign.Center
    )
    ComposableFlipperMockup(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 32.dp),
        flipperColor = flipperColor,
        isActive = false,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .placeholderConnecting()
            )
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableWaitingFlipperConnectionPreview() {
    FlipperThemeInternal {
        Box {
            ComposableWaitingFlipperConnectionInternal(
                flipperColor = HardwareColor.TRANSPARENT
            )
        }
    }
}
