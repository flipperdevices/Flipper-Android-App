package com.flipperdevices.info.impl.compose.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.markdown.ClickableUrlText
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.FirmwareUpdateStatus
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel
import com.flipperdevices.info.shared.InfoElementCard

@Composable
fun ComposableFirmwareUpdate(
    modifier: Modifier,
    firmwareViewModel: FirmwareUpdateViewModel = viewModel()
) {
    val updateStatus by firmwareViewModel.getState().collectAsState()
    if (updateStatus is FirmwareUpdateStatus.Unsupported) {
        InfoElementCard(
            modifier = modifier,
            titleId = R.string.info_firmware_update_title
        ) {
            ComposableFirmwareUpdateUnsupported()
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFirmwareUpdateUnsupported() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(all = 12.dp),
            painter = painterResource(DesignSystem.drawable.ic_firmware_update),
            contentDescription = stringResource(R.string.info_firmware_update_unsupported_title)
        )
        Text(
            text = stringResource(R.string.info_firmware_update_unsupported_title),
            style = LocalTypography.current.bodyM14,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 8.dp, start = 12.dp, end = 12.dp),
            text = stringResource(R.string.info_firmware_update_unsupported_desc),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text40,
            textAlign = TextAlign.Center
        )

        ClickableUrlText(
            modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
            markdownResId = R.string.info_firmware_update_unsupported_link,
            style = LocalTypography.current.bodyM14.copy(
                color = LocalPallet.current.text60
            )
        )
    }
}
