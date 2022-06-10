package com.flipperdevices.info.impl.compose.bar

import com.flipperdevices.core.ui.res.R as DesignSystem
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

private const val EMPTY_BATTERY = 0f
private const val FIRST_BATTERY_THRESHOLD = 0.15f
private const val SECOND_BATTERY_THRESHOLD = 0.4f
private const val FULL_BATTERY = 1.0f

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
            contentDescription = null,
            tint = colorResource(R.color.battery_backgroud)
        )
    }
}

@Composable
private fun BatteryContent(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) percent: Float
) {
    val batteryColorId = when (percent) {
        in EMPTY_BATTERY..FIRST_BATTERY_THRESHOLD -> DesignSystem.color.red
        in FIRST_BATTERY_THRESHOLD..SECOND_BATTERY_THRESHOLD -> DesignSystem.color.yellow
        in SECOND_BATTERY_THRESHOLD..FULL_BATTERY -> DesignSystem.color.green
        else -> DesignSystem.color.red
    }

    Row(
        modifier
            .clip(RoundedCornerShape(3.dp))
            .background(colorResource(R.color.battery_backgroud))
            .padding(1.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White)
            .padding(1.66.dp)
            .clip(RoundedCornerShape(1.dp))
    ) {
        val remainingWeight = 1.0f - percent
        Box(
            Modifier
                .weight(weight = percent)
                .fillMaxHeight()
                .background(colorResource(batteryColorId))
        )
        if (remainingWeight > 0f) {
            Box(Modifier.weight(remainingWeight))
        }
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
