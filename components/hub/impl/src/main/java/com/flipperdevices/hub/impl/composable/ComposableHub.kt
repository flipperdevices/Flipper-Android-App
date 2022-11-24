package com.flipperdevices.hub.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.faphub.maincard.api.MainCardApi
import com.flipperdevices.hub.impl.R
import com.flipperdevices.hub.impl.composable.elements.NfcAttack
import com.flipperdevices.hub.impl.viewmodel.HubViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableHub(
    mainCardApi: MainCardApi,
    onOpenAttack: () -> Unit,
    onOpenFapHub: () -> Unit
) {
    val viewModel = tangleViewModel<HubViewModel>()
    val fapHubEnabled by viewModel.isFapHubEnabled().collectAsState()

    Column {
        OrangeAppBar(
            titleId = R.string.hub_title
        )
        if (fapHubEnabled) {
            mainCardApi.ComposableMainCard(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp),
                onClick = onOpenFapHub
            )
        }
        NfcAttack(onOpenAttack)
    }
}
