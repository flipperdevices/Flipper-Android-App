package com.flipperdevices.archive.impl.composable.key

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.bridge.dao.api.R as DaoR
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableFlipperKey(
    keyPath: FlipperKeyPath = FlipperKeyPath.DUMMY
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = {}
            ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComposableKeyIcon(keyPath.fileType)
            ComposableKeyDescription(modifier = Modifier.weight(1f), keyPath)
        }
    }
}

@Composable
private fun ComposableKeyIcon(fileType: FlipperFileType?) {
    val typeColor = fileType?.color ?: DaoR.color.fileformat_color_unknown
    val typeIcon = fileType?.icon ?: DaoR.drawable.ic_fileformat_unknown
    Box(
        modifier = Modifier
            .width(width = 56.dp)
            .fillMaxHeight()
            .background(colorResource(typeColor)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            painter = painterResource(typeIcon),
            tint = Color.White,
            contentDescription = stringResource(R.string.archive_key_icon_pic_desc)
        )
    }
}

@Composable
private fun ComposableKeyDescription(
    modifier: Modifier,
    keyPath: FlipperKeyPath
) {
    Column(
        modifier = modifier
            .padding(start = 15.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = keyPath.name,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = keyPath.fileType?.humanReadableName
                ?: stringResource(R.string.archive_key_type_unknown),
            fontWeight = FontWeight.Normal,
            color = colorResource(R.color.archive_description_keytype),
            fontSize = 16.sp
        )
    }
}
