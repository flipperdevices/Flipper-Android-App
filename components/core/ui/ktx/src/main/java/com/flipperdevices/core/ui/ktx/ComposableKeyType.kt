package com.flipperdevices.core.ui.ktx

import androidx.annotation.ColorRes
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.bridge.dao.api.R as DaoR
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
@Preview
fun ComposableKeyType(
    type: FlipperFileType? = FlipperFileType.NFC,
    @ColorRes colorId: Int? = type?.color,
    minWidth: Dp? = 110.dp
) {
    val icon = type?.icon ?: DaoR.drawable.ic_fileformat_unknown
    val color = colorId ?: DaoR.color.fileformat_color_unknown
    val title = type?.humanReadableName
        ?: stringResource(DaoR.string.fileformat_unknown)

    var rowModifier = Modifier
        .clip(RoundedCornerShape(bottomEnd = 18.dp))
        .background(colorResource(color))

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
            contentDescription = title
        )

        Text(
            modifier = Modifier.padding(
                end = 14.dp
            ),
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(DesignSystem.color.black_100)
        )
    }
}
