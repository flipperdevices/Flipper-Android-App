package com.flipperdevices.nfc.attack.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.nfc.attack.impl.R
import com.flipperdevices.nfc.attack.impl.composable.elements.MifareClassicComposable

@Composable
fun ComposableNfcAttack(
    onOpenMfKey32: () -> Unit,
    hasMfKey32Notification: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(modifier = modifier) {
        OrangeAppBar(
            titleId = R.string.nfcattack_title,
            onBack = onBack
        )
        MifareClassicComposable(hasMfKey32Notification, onOpenMfKey32)
    }
}
