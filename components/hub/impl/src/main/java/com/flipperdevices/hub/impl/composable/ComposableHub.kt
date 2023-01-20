package com.flipperdevices.hub.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.hub.impl.R
import com.flipperdevices.hub.impl.composable.elements.NfcAttack
import com.flipperdevices.hub.impl.viewmodel.HubViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableHub(
    mainCardComposable: @Composable () -> Unit,
    onOpenAttack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = tangleViewModel<HubViewModel>()
    val fapHubEnabled by viewModel.isFapHubEnabled().collectAsState()

    Column(modifier) {
        OrangeAppBar(
            titleId = R.string.hub_title
        )
        if (fapHubEnabled) {
            mainCardComposable()
        }
        NfcAttack(onOpenAttack)
    }
}
