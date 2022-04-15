package com.flipperdevices.updater.ui.composable

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.ui.R
import com.flipperdevices.updater.ui.viewmodel.UpdaterViewModel

@Composable
fun ComposableUpdateButton(
    updateCardState: UpdateCardState
) {
    val updaterViewModel = viewModel<UpdaterViewModel>()
    var buttonModifier = Modifier.padding(all = 12.dp)

    val updatingState by updaterViewModel.getState().collectAsState()
    val updatingStateLocal: UpdatingState = updatingState
    when (updatingStateLocal) {
        UpdatingState.NotStarted -> {}
        is UpdatingState.DownloadingFromNetwork -> {
            ComposableInProgressIndicator(
                updaterViewModel = updaterViewModel,
                accentColorId = R.color.update_green,
                secondColorId = R.color.update_green_background,
                iconId = R.drawable.ic_globe,
                percent = updatingStateLocal.percent,
                descriptionId = R.string.update_stage_downloading_desc
            )
            return
        }
        is UpdatingState.UploadOnFlipper -> {
            ComposableInProgressIndicator(
                updaterViewModel = updaterViewModel,
                accentColorId = DesignSystem.color.accent_secondary,
                secondColorId = R.color.update_blue_background,
                iconId = R.drawable.ic_bluetooth,
                percent = updatingStateLocal.percent,
                descriptionId = R.string.update_stage_uploading_desc
            )
            return
        }
        UpdatingState.Rebooting -> {
            Text("Rebooting now")
            return
        }
    }

    when (updateCardState) {
        UpdateCardState.Error -> return
        UpdateCardState.InProgress -> return
        is UpdateCardState.NoUpdate -> ComposableUpdateButton(
            buttonModifier,
            textId = R.string.update_button_no_updates,
            descriptionId = R.string.update_button_no_updates_desc,
            colorId = DesignSystem.color.black_20
        )
        is UpdateCardState.UpdateAvailable -> {
            buttonModifier = buttonModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { updaterViewModel.onStart(updateCardState.updaterDist) }
            )

            if (updateCardState.isOtherChannel) {
                ComposableUpdateButton(
                    buttonModifier,
                    textId = R.string.update_button_install,
                    descriptionId = R.string.update_button_install_desc,
                    colorId = DesignSystem.color.accent
                )
            } else ComposableUpdateButton(
                buttonModifier,
                textId = R.string.update_button_update,
                descriptionId = R.string.update_button_update_desc,
                colorId = R.color.update_green
            )
        }
    }
}

@Composable
private fun ComposableUpdateButton(
    buttonModifier: Modifier,
    @StringRes textId: Int,
    @StringRes descriptionId: Int,
    @ColorRes colorId: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = buttonModifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(3.dp))
                .background(colorResource(colorId)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = stringResource(textId),
                textAlign = TextAlign.Center,
                color = colorResource(DesignSystem.color.white_100),
                fontWeight = FontWeight.W400,
                fontSize = 40.sp,
                fontFamily = FontFamily(Font(R.font.flipper_bold))
            )
        }
        Text(
            modifier = Modifier.padding(top = 3.dp, bottom = 15.dp),
            text = stringResource(descriptionId),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            color = colorResource(DesignSystem.color.black_16)
        )
    }
}
