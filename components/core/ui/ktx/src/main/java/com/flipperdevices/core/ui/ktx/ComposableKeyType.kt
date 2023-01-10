package com.flipperdevices.core.ui.ktx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.R
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType.Companion.colorByFlipperKeyType
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
@Preview
fun ComposableKeyType(
    type: FlipperKeyType? = FlipperKeyType.NFC,
    colorKey: Color = colorByFlipperKeyType(type),
    minWidth: Dp? = 110.dp
) {
    val icon = type?.icon ?: DesignSystem.drawable.ic_fileformat_unknown
    val title = type?.humanReadableName
        ?: stringResource(R.string.fileformat_unknown)

    var rowModifier = Modifier
        .clip(RoundedCornerShape(bottomEnd = 18.dp))
        .placeholderByLocalProvider()
        .background(colorKey)

    if (minWidth != null) {
        rowModifier = rowModifier.defaultMinSize(minWidth = minWidth)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(all = 8.dp)
                .size(size = 24.dp),
            painter = painterResource(icon),
            contentDescription = title,
            tint = LocalPallet.current.keyIcon
        )

        Text(
            modifier = Modifier.padding(end = 14.dp),
            text = title,
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.keyTitle
        )
    }
}
