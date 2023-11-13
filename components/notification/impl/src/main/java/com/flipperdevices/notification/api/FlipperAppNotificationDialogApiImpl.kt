package com.flipperdevices.notification.api

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.notification.impl.R
import com.flipperdevices.notification.viewmodel.NotificationDialogViewModel
import com.squareup.anvil.annotations.ContributesBinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperAppNotificationDialogApi::class)
class FlipperAppNotificationDialogApiImpl @Inject constructor() : FlipperAppNotificationDialogApi {
    @Composable
    override fun NotificationDialog() {
        val dialogViewModel = tangleViewModel<NotificationDialogViewModel>()
        val isDialogShown by dialogViewModel.isNotificationShown().collectAsState()

        if (isDialogShown) {
            val imageId = if (MaterialTheme.colors.isLight) {
                R.drawable.pic_notification_light
            } else {
                R.drawable.pic_notification_dark
            }
            val dialog = remember(imageId, dialogViewModel) {
                FlipperMultiChoiceDialogModel.Builder()
                    .setImage {
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = painterResourceByKey(imageId),
                            contentDescription = stringResource(R.string.notification_dialog_title)
                        )
                    }.setTitle(R.string.notification_dialog_title)
                    .setDescription(R.string.notification_dialog_desc).addButton(
                        textId = R.string.notification_dialog_btn_enable,
                        onClick = dialogViewModel::onEnableNotification,
                        isActive = true
                    ).addButton(
                        textId = R.string.notification_dialog_btn_skip,
                        onClick = dialogViewModel::onDismiss
                    )
                    .setCloseOnClickOutside(true)
                    .setOnDismissRequest(dialogViewModel::onDismiss)
                    .build()
            }
            FlipperMultiChoiceDialog(model = dialog)
        }
    }
}
