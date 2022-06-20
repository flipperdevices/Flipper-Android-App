package com.flipperdevices.info.shared

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableDeviceInfoRowWithText(
    @StringRes titleId: Int,
    inProgress: Boolean,
    value: String?
) {
    ComposableDeviceInfoRowWithText(stringResource(titleId), inProgress, value)
}

@Composable
fun ComposableDeviceInfoRowWithText(
    text: String,
    inProgress: Boolean,
    value: String?
) {
    if (value == null) {
        ComposableDeviceInfoRow(text, inProgress, null)
        return
    }
    ComposableDeviceInfoRow(text, inProgress) {
        ComposableDeviceInfoRowText(modifier = it, text = value)
    }
}

@Composable
fun ComposableDeviceInfoRow(
    text: String,
    inProgress: Boolean,
    content: (@Composable (Modifier) -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = colorResource(DesignSystem.color.black_30),
            fontSize = 14.sp,
            fontWeight = FontWeight.W400
        )
        if (content != null) {
            content(Modifier)
        } else if (inProgress) {
            DeviceInfoRowPlaceholder()
        } else ComposableDeviceInfoRowText(
            Modifier,
            stringResource(R.string.info_device_unknown)
        )
    }
}

@Composable
fun ComposableDeviceInfoRow(
    @StringRes titleId: Int,
    inProgress: Boolean,
    content: (@Composable (Modifier) -> Unit)?
) {
    ComposableDeviceInfoRow(stringResource(titleId), inProgress, content)
}

@Composable
fun ComposableDeviceInfoRowText(
    modifier: Modifier = Modifier,
    text: String,
    @ColorRes colorId: Int = DesignSystem.color.black_100
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        color = colorResource(colorId),
        fontWeight = FontWeight.W400
    )
}

@Composable
private fun DeviceInfoRowPlaceholder() {
    Box(
        modifier = Modifier
            .height(16.dp)
            .width(50.dp)
            .placeholderConnecting()
    )
}
