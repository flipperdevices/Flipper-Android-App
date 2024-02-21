package com.flipperdevices.info.shared

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableDeviceInfoRowWithText(
    @StringRes titleId: Int,
    inProgress: Boolean,
    value: String?,
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    ComposableDeviceInfoRowWithText(stringResource(titleId), inProgress, value, modifier, color)
}

@Composable
@Suppress("ModifierReused")
fun ComposableDeviceInfoRowWithText(
    text: String,
    inProgress: Boolean,
    value: String?,
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    if (value == null) {
        ComposableDeviceInfoRow(
            modifier = modifier,
            text = text,
            inProgress = inProgress,
            content = null
        )
        return
    }
    ComposableDeviceInfoRow(
        modifier = modifier,
        text = text,
        inProgress = inProgress,
        content = {
            ComposableDeviceInfoRowText(modifier = it, text = value, color = color)
        }
    )
}

@Composable
fun ComposableDeviceInfoRow(
    text: String,
    inProgress: Boolean,
    content: (@Composable (Modifier) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = LocalPallet.current.text30,
            style = LocalTypography.current.bodyR14
        )
        if (content != null) {
            content(Modifier)
        } else if (inProgress) {
            DeviceInfoRowPlaceholder()
        } else {
            ComposableDeviceInfoRowText(
                stringResource(R.string.info_device_unknown)
            )
        }
    }
}

@Composable
fun ComposableDeviceInfoRow(
    @StringRes titleId: Int,
    inProgress: Boolean,
    content: (@Composable (Modifier) -> Unit)?,
    modifier: Modifier = Modifier
) {
    ComposableDeviceInfoRow(stringResource(titleId), inProgress, content, modifier)
}

@Composable
fun ComposableDeviceInfoRowText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null
) {
    Text(
        modifier = modifier,
        text = text,
        color = color ?: LocalPallet.current.text100,
        textAlign = TextAlign.Right,
        style = LocalTypography.current.bodyR14
    )
}

@Composable
fun ComposableLongDeviceInfoRowText(
    text: String,
    modifier: Modifier = Modifier,
    lines: Int = 1,
    color: Color = LocalPallet.current.text100
) {
    Text(
        modifier = modifier.padding(start = 24.dp),
        text = text,
        color = color,
        maxLines = lines,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Right,
        style = LocalTypography.current.bodyR14
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
