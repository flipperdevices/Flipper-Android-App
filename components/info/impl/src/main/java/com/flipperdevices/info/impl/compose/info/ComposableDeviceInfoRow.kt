package com.flipperdevices.info.impl.compose.info

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.R

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
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.padding(all = 12.dp),
            text = text,
            color = colorResource(DesignSystem.color.black_30),
            fontSize = 14.sp,
            fontWeight = FontWeight.W400
        )
        val contentModifier = Modifier
            .weight(1f)
            .padding(end = 12.dp)
        if (content != null) {
            content(
                Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )
        } else if (inProgress) {
            DeviceInfoRowProgressBar(contentModifier)
        } else ComposableDeviceInfoRowText(
            contentModifier,
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
    modifier: Modifier,
    text: String,
    @ColorRes colorId: Int = DesignSystem.color.black_100
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        color = colorResource(colorId),
        fontWeight = FontWeight.W400,
        textAlign = TextAlign.End
    )
}

@Composable
private fun DeviceInfoRowProgressBar(modifier: Modifier) {
    Box(
        modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = colorResource(DesignSystem.color.black_30),
            strokeWidth = 1.dp
        )
    }
}
