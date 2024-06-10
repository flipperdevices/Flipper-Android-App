package com.flipperdevices.toolstab.impl.composable.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.info.shared.ComposableOneRowCard
import com.flipperdevices.toolstab.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun NfcAttack(
    onOpenAttack: () -> Unit,
    notificationCount: Int,
    modifier: Modifier = Modifier
) {
    ComposableOneRowCard(
        iconId = DesignSystem.drawable.ic_fileformat_nfc,
        onOpen = onOpenAttack,
        titleId = R.string.toolstab_hfc_title,
        descriptionId = R.string.toolstab_hfc_desc,
        notificationCount = notificationCount,
        modifier = modifier
    )
}
