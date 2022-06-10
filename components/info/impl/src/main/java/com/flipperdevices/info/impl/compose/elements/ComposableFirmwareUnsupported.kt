package com.flipperdevices.info.impl.compose.elements

import com.flipperdevices.core.ui.res.R as DesignSystem
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.markdown.ClickableUrlText
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.FirmwareUpdateStatus
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel

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
            painter = painterResource(R.drawable.ic_firmware_update),
            contentDescription = stringResource(R.string.info_firmware_update_unsupported_title)
        )
        Text(
            text = stringResource(R.string.info_firmware_update_unsupported_title),
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(DesignSystem.color.black_100),
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 8.dp, start = 12.dp, end = 12.dp),
            text = stringResource(R.string.info_firmware_update_unsupported_desc),
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            color = colorResource(DesignSystem.color.black_40),
            textAlign = TextAlign.Center
        )

        ClickableUrlText(
            modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
            markdownResId = R.string.info_firmware_update_unsupported_link,
            style = TextStyle(
                color = colorResource(DesignSystem.color.black_60),
                fontSize = 14.sp,
                fontWeight = FontWeight.W500
            )
        )
    }
}
