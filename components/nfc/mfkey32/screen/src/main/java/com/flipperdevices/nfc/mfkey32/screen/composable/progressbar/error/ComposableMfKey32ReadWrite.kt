package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.error

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableMfKey32ReadWrite(
    modifier: Modifier = Modifier
) = ComposableMfKey32ErrorContent(
    modifier = modifier,
    titleId = R.string.mfkey32_error_readwrite_title,
    picId = DesignSystem.drawable.pic_flipper_no_sd_error_white,
    picIdBlack = DesignSystem.drawable.pic_flipper_no_sd_error_white,
    content = {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            text = stringResource(R.string.mfkey32_error_readwrite_desc),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyR16,
            color = LocalPallet.current.text40
        )
    }
)

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableMfKey32ReadWritePreview() {
    FlipperThemeInternal {
        ComposableMfKey32ReadWrite()
    }
}
