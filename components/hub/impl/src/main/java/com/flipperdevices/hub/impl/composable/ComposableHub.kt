package com.flipperdevices.hub.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.hub.impl.R
import com.flipperdevices.hub.impl.composable.elements.NfcAttack

@Composable
fun ComposableHub(onOpenAttack: () -> Unit) {
    Column {
        OrangeAppBar(
            titleId = R.string.hub_title
        )
        NfcAttack(onOpenAttack)
    }
}