package com.flipperdevices.hub.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.hub.impl.R
import com.flipperdevices.hub.impl.composable.elements.NfcAttack
import com.flipperdevices.hub.impl.viewmodel.HubViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableHub(
    mainCardComposable: @Composable () -> Unit,
    onOpenAttack: () -> Unit
) {
    val viewModel = tangleViewModel<HubViewModel>()
    val fapHubEnabled by viewModel.isFapHubEnabled().collectAsState()

    Column {
        OrangeAppBar(
            titleId = R.string.hub_title
        )
        if (fapHubEnabled) {
            mainCardComposable()
        }
        NfcAttack(onOpenAttack)
    }
}
