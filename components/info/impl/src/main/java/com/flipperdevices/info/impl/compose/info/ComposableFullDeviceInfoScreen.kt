package com.flipperdevices.info.impl.compose.info

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ktx.jre.titlecaseFirstCharIfItIsLowercase
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.elements.InfoElementCard
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel

@Composable
fun ComposableFullDeviceInfoScreen(
    deviceInfoViewModel: DeviceInfoViewModel = viewModel()
) {
    val verboseDeviceInfo by deviceInfoViewModel.getVerboseDeviceInfoState().collectAsState()
    val fieldEntriesFiltered = verboseDeviceInfo
        .rpcInformationMap.entries.filterNot { it.key.isBlank() || it.value.isBlank() }

    Column {
        ComposableFullDeviceInfoScreenBar()
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            InfoElementCard(
                Modifier.padding(top = 14.dp),
                R.string.info_device_info_title_card
            ) {
                ComposableInfoCardContent(isUnsupported = false)
            }
            if (fieldEntriesFiltered.isNotEmpty()) {
                InfoElementCard(
                    Modifier.padding(top = 14.dp, bottom = 14.dp),
                    R.string.info_device_info_other_card
                ) {
                    ComposableOtherCardContent(fieldEntriesFiltered)
                }
            }
        }
    }
}

@Composable
private fun ComposableOtherCardContent(
    fields: List<Map.Entry<String, String>>
) {
    fields.forEachIndexed { index, field ->
        val name = field.key.split(' ', '_')
            .map { it.titlecaseFirstCharIfItIsLowercase() }
            .joinToString(" ")
        val value = field.value

        ComposableDeviceInfoRowWithText(text = name, inProgress = false, value = value)
        if (index != fields.size - 1) {
            ComposableInfoDivider()
        }
    }
}

@Composable
private fun ComposableFullDeviceInfoScreenBar() {
    val router = LocalRouter.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorResource(DesignSystem.color.accent)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clickable(
                    indication = rememberRipple(),
                    onClick = { router.exit() },
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 10.dp),
            painter = painterResource(DesignSystem.drawable.ic_back),
            contentDescription = null,
            tint = colorResource(DesignSystem.color.black_100)
        )
        Text(
            text = stringResource(R.string.info_device_info_title),
            fontWeight = FontWeight.W700,
            fontSize = 20.sp,
            color = colorResource(DesignSystem.color.black_100)
        )
    }
}
