package com.flipperdevices.hub.impl.composable.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.hub.impl.R
import com.flipperdevices.hub.impl.viewmodel.NfcAttackViewModel
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun NfcAttack(
    onOpenAttack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nfcAttackViewModel: NfcAttackViewModel = tangleViewModel()
    val notificationCount by nfcAttackViewModel.getNfcAttackNotificationCountState()
        .collectAsState()

    ComposableHubElement(
        iconId = DesignSystem.drawable.ic_fileformat_nfc,
        onOpen = onOpenAttack,
        titleId = R.string.hub_hfc_title,
        descriptionId = R.string.hub_hfc_desc,
        notificationCount = notificationCount,
        modifier = modifier
    )
}
