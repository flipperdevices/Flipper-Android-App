package com.flipperdevices.info.impl.compose.updater

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
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.fonts.R as Fonts
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.VersionFiles

@Composable
fun ComposableUpdateButton(
    updaterUIApi: UpdaterUIApi,
    updateCardState: UpdateCardState
) {
    var buttonModifier = Modifier.padding(all = 12.dp)

    when (updateCardState) {
        is UpdateCardState.Error -> return
        UpdateCardState.InProgress -> return
        is UpdateCardState.NoUpdate -> ComposableUpdateButtonContent(
            buttonModifier,
            textId = R.string.info_device_updater_button_no_updates,
            descriptionId = R.string.info_device_updater_button_no_updates_desc,
            colorId = DesignSystem.color.black_20
        )
        is UpdateCardState.UpdateAvailable -> {
            buttonModifier = buttonModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = {
                    updaterUIApi.openUpdateScreen(
                        silent = false,
                        VersionFiles(
                            updateCardState.lastVersion,
                            updateCardState.updaterDist
                        )
                    )
                }
            )

            if (updateCardState.isOtherChannel) {
                ComposableUpdateButtonContent(
                    buttonModifier,
                    textId = R.string.info_device_updater_button_install,
                    descriptionId = R.string.info_device_updater_button_install_desc,
                    colorId = DesignSystem.color.accent
                )
            } else ComposableUpdateButtonContent(
                buttonModifier,
                textId = R.string.info_device_updater_button_update,
                descriptionId = R.string.info_device_updater_button_update_desc,
                colorId = R.color.update_green
            )
        }
    }
}

@Composable
private fun ComposableUpdateButtonContent(
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
                fontFamily = FontFamily(Font(Fonts.font.flipper_bold))
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
