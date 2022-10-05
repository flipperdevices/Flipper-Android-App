package com.flipperdevices.updater.card.composable.pending

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.composable.ComposableUpdateButtonContent
import com.flipperdevices.updater.card.model.UpdatePending
import com.flipperdevices.updater.model.UpdateCardState

@Composable
fun ComposableUpdateButtonContentChooseFile(
    buttonModifier: Modifier,
    updateCardState: UpdateCardState,
    onChoose: (UpdatePending) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && updateCardState is UpdateCardState.CustomUpdate) {
            onChoose(UpdatePending.URI(uri, context, updateCardState.flipperVersion))
        }
    }
    ComposableUpdateButtonContent(
        buttonModifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = { launcher.launch("*/*") }
        ),
        textId = R.string.updater_card_updater_button_choose_file,
        descriptionId = R.string.updater_card_updater_button_choose_file_desc,
        color = LocalPallet.current.accent
    )
}
