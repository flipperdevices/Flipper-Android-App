package com.flipperdevices.nfc.attack.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.nfc.attack.impl.R
import com.flipperdevices.nfc.attack.impl.composable.elements.MifareClassicComposable
import com.flipperdevices.nfc.attack.impl.viewmodel.NfcAttackViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableNfcAttack(
    onOpenMfKey32: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: NfcAttackViewModel = tangleViewModel()
    Column {
        OrangeAppBar(
            titleId = R.string.nfcattack_title,
            onBack = onBack
        )
        MifareClassicComposable(viewModel, onOpenMfKey32)
    }
}
