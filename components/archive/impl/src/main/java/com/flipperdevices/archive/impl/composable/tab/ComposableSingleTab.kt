package com.flipperdevices.archive.impl.composable.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.composable.tab.helper.RectWithCornerShape
import com.flipperdevices.archive.impl.model.ArchiveTab

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableSingleTab(
    tab: ArchiveTab = ArchiveTab(),
    isActive: Boolean = true
) {
    val textColor = if (isActive) {
        colorResource(R.color.tab_active_color)
    } else {
        colorResource(R.color.tab_inactive_color)
    }

    Box(
        modifier = Modifier.defaultMinSize(minWidth = 72.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = tab.name,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(
                start = 4.dp,
                end = 4.dp,
                top = 17.dp,
                bottom = 13.dp
            ),
            color = textColor
        )

        if (isActive) {
            Box(
                modifier = Modifier
                    .size(width = 66.dp, height = 4.dp)
                    .clip(RectWithCornerShape())
                    .background(colorResource(R.color.tab_active_color))
            )
        }
    }
}
