package com.flipperdevices.updater.card.composable

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.composable.dialogs.ComposableUpdateRequest
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.updater.fonts.R as Fonts

@Composable
fun ComposableUpdateButton(
    updateCardState: UpdateCardState,
    inProgress: Boolean
) {
    var buttonModifier = Modifier.padding(all = 12.dp)
    var pendingUpdateRequest by remember {
        mutableStateOf<UpdateCardState.UpdateAvailable?>(null)
    }
    val localUpdaterRequest = pendingUpdateRequest

    if (inProgress) {
        ComposableUpdateButtonPlaceholder(buttonModifier)
        return
    }

    if (localUpdaterRequest != null) {
        ComposableUpdateRequest(pendingUpdateRequest = localUpdaterRequest) {
            pendingUpdateRequest = null
        }
    }

    when (updateCardState) {
        is UpdateCardState.Error -> return
        UpdateCardState.InProgress -> return
        is UpdateCardState.NoUpdate -> ComposableUpdateButtonContent(
            buttonModifier,
            textId = R.string.updater_card_updater_button_no_updates,
            descriptionId = R.string.updater_card_updater_button_no_updates_desc,
            colorId = DesignSystem.color.black_20
        )
        is UpdateCardState.UpdateAvailable -> {
            buttonModifier = buttonModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = {
                    pendingUpdateRequest = updateCardState
                }
            )

            if (updateCardState.isOtherChannel) {
                ComposableUpdateButtonContent(
                    buttonModifier,
                    textId = R.string.updater_card_updater_button_install,
                    descriptionId = R.string.updater_card_updater_button_install_desc,
                    colorId = DesignSystem.color.accent
                )
            } else ComposableUpdateButtonContent(
                buttonModifier,
                textId = R.string.updater_card_updater_button_update,
                descriptionId = R.string.updater_card_updater_button_update_desc,
                colorId = R.color.update_green
            )
        }
    }
}

@Composable
fun ComposableUpdateButtonPlaceholder(buttonModifier: Modifier) {
    Box(
        modifier = buttonModifier
            .height(46.dp)
            .fillMaxWidth()
            .placeholderConnecting(shape = 9)
    )
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
                .clip(RoundedCornerShape(9.dp))
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
            modifier = Modifier.padding(
                top = 3.dp,
                bottom = 15.dp,
                start = 12.dp,
                end = 12.dp
            ),
            text = stringResource(descriptionId),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            color = colorResource(DesignSystem.color.black_16)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableUpdateButtonPreview() {
    val version = FirmwareVersion(
        channel = FirmwareChannel.RELEASE,
        version = "1.1.1"
    )
    val updateCardState = setOf(
        UpdateCardState.NoUpdate(flipperVersion = version),
        UpdateCardState.UpdateAvailable(
            fromVersion = version,
            lastVersion = version,
            updaterDist = DistributionFile(
                url = "123",
                sha256 = "123"
            ),
            isOtherChannel = false
        )
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).background(colorResource(id = DesignSystem.color.background))
    ) {
        updateCardState.forEach {
            ComposableUpdateButton(it, false)
            ComposableUpdateButton(it, true)
        }
    }
}
