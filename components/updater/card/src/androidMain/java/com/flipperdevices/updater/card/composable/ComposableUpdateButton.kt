package com.flipperdevices.updater.card.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.composable.dialogs.ComposableUpdateRequest
import com.flipperdevices.updater.card.model.UpdatePending
import com.flipperdevices.updater.card.viewmodel.UpdateRequestViewModel
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateRequest

@Composable
fun ComposableUpdateButton(
    updateRequestViewModel: UpdateRequestViewModel,
    updateCardState: UpdateCardState,
    inProgress: Boolean,
    modifier: Modifier = Modifier,
    onStartUpdateRequest: (UpdateRequest) -> Unit
) {
    var buttonModifier = modifier.padding(all = 12.dp)
    if (inProgress) {
        ComposableUpdateButtonPlaceholder(buttonModifier)
        return
    }
    var pendingUpdateRequest by remember { mutableStateOf<UpdatePending?>(null) }
    val localUpdaterRequest = pendingUpdateRequest
    if (localUpdaterRequest != null) {
        ComposableUpdateRequest(
            pendingUpdateRequest = localUpdaterRequest,
            onStartUpdateRequest = onStartUpdateRequest,
            updateRequestViewModel = updateRequestViewModel
        ) {
            pendingUpdateRequest = null
        }
    }

    when (updateCardState) {
        is UpdateCardState.Error -> return
        UpdateCardState.InProgress -> return
        is UpdateCardState.NoUpdate -> ComposableUpdateButtonContent(
            buttonModifier = buttonModifier,
            textId = R.string.updater_card_updater_button_no_updates,
            descriptionId = R.string.updater_card_updater_button_no_updates_desc,
            color = LocalPallet.current.text20
        )

        is UpdateCardState.UpdateFromFile -> ComposableUpdateButtonContentChooseFile(
            modifier = buttonModifier,
            updateCardState = updateCardState,
            onChoose = { pendingUpdateRequest = it }
        )

        is UpdateCardState.UpdateAvailable -> {
            buttonModifier = buttonModifier.clickableRipple {
                pendingUpdateRequest = UpdatePending.Request(updateCardState.update)
            }

            if (updateCardState.isOtherChannel) {
                ComposableUpdateButtonContent(
                    buttonModifier = buttonModifier,
                    textId = R.string.updater_card_updater_button_install,
                    descriptionId = R.string.updater_card_updater_button_install_desc,
                    color = LocalPallet.current.accent
                )
            } else {
                ComposableUpdateButtonContent(
                    buttonModifier = buttonModifier,
                    textId = R.string.updater_card_updater_button_update,
                    descriptionId = R.string.updater_card_updater_button_update_desc,
                    color = LocalPallet.current.updateProgressGreen
                )
            }
        }
    }
}

@Composable
private fun ComposableUpdateButtonPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .fillMaxWidth()
            .placeholderConnecting(shape = 9)
    )
}

@Composable
fun ComposableUpdateButtonContent(
    @StringRes textId: Int,
    @StringRes descriptionId: Int,
    color: Color,
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = buttonModifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(9.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = stringResource(textId),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.updateButton40,
                color = LocalPallet.current.onFirmwareUpdateButton
            )
        }
        Text(
            modifier = Modifier.padding(
                top = 3.dp,
                bottom = 8.dp,
                start = 12.dp,
                end = 12.dp
            ),
            text = stringResource(descriptionId),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.subtitleR12,
            color = LocalPallet.current.text16
        )
    }
}
