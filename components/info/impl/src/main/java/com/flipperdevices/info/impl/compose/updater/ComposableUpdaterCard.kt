package com.flipperdevices.info.impl.compose.updater

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.ktx.painterResourceByKey
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.elements.InfoElementCard
import com.flipperdevices.info.impl.compose.info.ComposableDeviceInfoRow
import com.flipperdevices.info.impl.compose.info.ComposableInfoDivider
import com.flipperdevices.info.impl.compose.info.ComposableUpdaterFirmwareVersionWithChoice
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.updater.api.UpdateCardApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdatingState

@Composable
fun ComposableUpdaterCard(
    modifier: Modifier,
    updaterUiApi: UpdaterUIApi,
    updaterApi: UpdaterApi,
    deviceStatusViewModel: DeviceStatusViewModel = viewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()

    if (deviceStatus !is DeviceStatus.Connected) {
        val updateStatus by updaterApi.getState().collectAsState()

        if (updateStatus.state == UpdatingState.Rebooting) {
            ComposableUpdaterReboot(modifier)
        }

        return
    }

    InfoElementCard(
        modifier = modifier,
        titleId = R.string.info_device_updater_title
    ) {
        val updateCardApi = updaterUiApi.getUpdateCardApi()
        val cardState by updateCardApi.getUpdateCardState().collectAsState()
        val cardStateLocal = cardState

        when (cardStateLocal) {
            is UpdateCardState.Error -> {
                ComposableFirmwareUpdaterError(updateCardApi, cardStateLocal)
            }
            UpdateCardState.InProgress -> ComposableFirmwareUpdaterInProgress()
            is UpdateCardState.NoUpdate -> ComposableFirmwareUpdaterContent(
                updaterUiApi,
                version = cardStateLocal.flipperVersion,
                updateCardState = cardStateLocal
            )
            is UpdateCardState.UpdateAvailable -> ComposableFirmwareUpdaterContent(
                updaterUiApi,
                version = cardStateLocal.lastVersion,
                updateCardState = cardStateLocal
            )
        }
    }
}

@Composable
private fun ComposableFirmwareUpdaterError(
    updateCardApi: UpdateCardApi,
    error: UpdateCardState.Error
) {
    val title = stringResource(error.titleId)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResourceByKey(error.iconId),
            contentDescription = title
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp),
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(DesignSystem.color.black_100),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
            text = stringResource(error.descriptionId),
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_30),
            textAlign = TextAlign.Center
        )
    }

    Text(
        modifier = Modifier
            .clickable(
                indication = rememberRipple(),
                onClick = updateCardApi::retry,
                interactionSource = remember { MutableInteractionSource() }
            )
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        text = stringResource(R.string.info_device_updater_error_retry),
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        color = colorResource(DesignSystem.color.accent_secondary)
    )
}

@Composable
private fun ComposableFirmwareUpdaterInProgress() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(top = 55.dp)
                .size(24.dp),
            color = colorResource(DesignSystem.color.accent_secondary),
            strokeWidth = 2.dp
        )
        Text(
            modifier = Modifier.padding(top = 14.dp, bottom = 55.dp),
            text = stringResource(R.string.info_device_updater_progress),
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_30),
            fontWeight = FontWeight.W500
        )
    }
}

@Composable
private fun ComposableFirmwareUpdaterContent(
    updaterUiApi: UpdaterUIApi,
    version: FirmwareVersion,
    updateCardState: UpdateCardState
) {
    ComposableDeviceInfoRow(titleId = R.string.info_device_updater_channel, inProgress = false) {
        ComposableUpdaterFirmwareVersionWithChoice(
            modifier = it,
            updaterUIApi = updaterUiApi,
            version = version
        )
    }
    ComposableInfoDivider()
    ComposableUpdateButton(updaterUiApi, updateCardState)
}
