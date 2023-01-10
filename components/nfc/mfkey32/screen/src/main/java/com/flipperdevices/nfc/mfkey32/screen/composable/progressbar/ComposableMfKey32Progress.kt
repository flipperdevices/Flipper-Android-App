package com.flipperdevices.nfc.mfkey32.screen.composable.progressbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flipperdevices.core.ui.ktx.FlipperProgressIndicator
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.error.ComposableMfKey32Error
import com.flipperdevices.nfc.mfkey32.screen.model.MfKey32State
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableMfKey32Progress(navController: NavController, state: MfKey32State) {
    when (state) {
        is MfKey32State.Calculating -> ComposableMfKey32ProgressInternal(
            titleId = R.string.mfkey32_calculation_title,
            descriptionId = R.string.mfkey32_calculation_desc,
            iconId = DesignSystem.drawable.pic_key,
            percent = state.percent,
            accentColor = LocalPallet.current.calculationMfKey32,
            secondColor = LocalPallet.current.calculationMfKey32Background
        )
        is MfKey32State.DownloadingRawFile -> ComposableMfKey32ProgressInternal(
            titleId = R.string.mfkey32_downloading_title,
            descriptionId = R.string.mfkey32_downloading_desc,
            iconId = DesignSystem.drawable.pic_download,
            percent = state.percent,
            accentColor = LocalPallet.current.actionOnFlipperEnable,
            secondColor = LocalPallet.current.actionOnFlipperProgress
        )
        MfKey32State.Uploading -> ComposableMfKey32ProgressInternal(
            titleId = R.string.mfkey32_uploading_title,
            descriptionId = R.string.mfkey32_uploading_desc,
            iconId = DesignSystem.drawable.pic_key,
            percent = null,
            accentColor = LocalPallet.current.calculationMfKey32,
            secondColor = LocalPallet.current.calculationMfKey32Background
        )
        is MfKey32State.Error -> {
            ComposableMfKey32Error(state.errorType)
            return
        }
        is MfKey32State.Saved -> if (state.keys.isEmpty()) {
            NotFoundCompleteAttack(navController::popBackStack)
        } else {
            CompleteAttack(
                state.keys,
                navController::popBackStack
            )
        }
    }

    GrayDivider()
}

@Composable
private fun GrayDivider() = Box(
    modifier = Modifier
        .height(1.dp)
        .fillMaxWidth()
        .padding(horizontal = 14.dp)
        .background(LocalPallet.current.text4)
)

@Composable
private fun ComposableMfKey32ProgressInternal(
    @StringRes titleId: Int,
    @StringRes descriptionId: Int,
    @DrawableRes iconId: Int,
    percent: Float?,
    accentColor: Color,
    secondColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(18.dp),
            text = stringResource(titleId),
            style = LocalTypography.current.titleB18,
            color = LocalPallet.current.text100
        )
        if (percent != null) {
            val animatedProgress by animateFloatAsState(
                targetValue = percent,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )

            FlipperProgressIndicator(
                modifier = Modifier.padding(horizontal = 32.dp),
                accentColor = accentColor,
                secondColor = secondColor,
                iconId = iconId,
                percent = animatedProgress
            )
        } else {
            FlipperProgressIndicator(
                modifier = Modifier.padding(horizontal = 32.dp),
                accentColor = accentColor,
                secondColor = secondColor,
                iconId = iconId,
                percent = null
            )
        }
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 18.dp, end = 18.dp, bottom = 18.dp),
            text = stringResource(descriptionId),
            style = LocalTypography.current.subtitleR12,
            color = LocalPallet.current.text40
        )
    }
}
