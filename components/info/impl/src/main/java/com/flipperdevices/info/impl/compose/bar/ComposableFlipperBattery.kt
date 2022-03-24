package com.flipperdevices.info.impl.compose

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.info.impl.R

@Composable
fun ComposableFlipperBattery(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) percent: Float
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BatteryContent(modifier, percent)
        Icon(
            modifier = Modifier.padding(start = 1.dp),
            painter = painterResource(R.drawable.ic_battery_pin),
            contentDescription = null
        )
    }
}

@Composable
private fun BatteryContent(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) percent: Float
) {
    Row(
        modifier
            .clip(RoundedCornerShape(3.dp))
            .background(colorResource(R.color.battery_backgroud))
            .padding(1.dp)
            .background(Color.White)
            .padding(1.66.dp)
            .clip(RoundedCornerShape(1.dp))
    ) {
        val remainingWeight = 1.0f - percent
        Box(
            Modifier
                .weight(weight = percent)
                .fillMaxHeight()
                .background(colorResource(R.color.battery_charged))
        )
        Box(Modifier.weight(remainingWeight))
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposableFlipperBatteryPreview() {
    Box {
        ComposableFlipperBattery(
            Modifier.size(width = 30.dp, height = 14.dp),
            percent = 1.0f
        )
    }
}
