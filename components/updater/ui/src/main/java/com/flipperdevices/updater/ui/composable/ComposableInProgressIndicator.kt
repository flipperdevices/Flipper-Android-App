package com.flipperdevices.updater.ui.composable

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.painterResourceByKey
import com.flipperdevices.updater.ui.R
import com.flipperdevices.updater.ui.viewmodel.UpdaterViewModel
import kotlin.math.roundToInt

private const val PERCENT_MAX = 100

@Composable
@Suppress("LongParameterList")
fun ComposableInProgressIndicator(
    updaterViewModel: UpdaterViewModel,
    @ColorRes accentColorId: Int,
    @ColorRes secondColorId: Int,
    @DrawableRes iconId: Int,
    percent: Float,
    @StringRes descriptionId: Int
) {
    val description = stringResource(descriptionId)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ComposableProgressBar(accentColorId, secondColorId, iconId, percent, description)
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = description,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            color = colorResource(DesignSystem.color.black_16)
        )
        Text(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = { updaterViewModel.onCancel() }
                )
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
            text = stringResource(R.string.update_button_cancel),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(DesignSystem.color.accent_secondary),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ComposableProgressBar(
    @ColorRes accentColorId: Int,
    @ColorRes secondColorId: Int,
    @DrawableRes iconId: Int,
    percent: Float,
    description: String
) {
    val accentColor = colorResource(accentColorId)
    val secondColor = colorResource(secondColorId)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(secondColor)
            .border(3.dp, accentColor, RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
        ) {
            val remainingWeight = 1.0f - percent

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(percent)
                    .background(accentColor)
            )

            if (remainingWeight > 0.0f) {
                Box(
                    modifier = Modifier
                        .weight(remainingWeight)
                )
            }
        }

        Icon(
            modifier = Modifier
                .padding(8.dp)
                .size(28.dp),
            painter = painterResourceByKey(iconId),
            contentDescription = description,
            tint = colorResource(DesignSystem.color.white_100)
        )

        Text(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 6.dp)
                .fillMaxWidth(),
            text = "${(percent * PERCENT_MAX).roundToInt()}%",
            textAlign = TextAlign.Center,
            color = colorResource(DesignSystem.color.white_100),
            fontWeight = FontWeight.W400,
            fontSize = 40.sp,
            fontFamily = FontFamily(Font(R.font.flipper))
        )
    }
}
