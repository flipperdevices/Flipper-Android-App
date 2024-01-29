package com.flipperdevices.hub.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.hub.impl.R
import com.flipperdevices.hub.impl.composable.elements.ComposableRemoteControl
import com.flipperdevices.hub.impl.composable.elements.NfcAttack

@Composable
fun ComposableHub(
    notificationCount: Int,
    mainCardComposable: @Composable () -> Unit,
    onOpenAttack: () -> Unit,
    onOpenRemoteControl: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        OrangeAppBar(
            titleId = R.string.hub_title
        )
        mainCardComposable()
        Row(
            modifier = Modifier
                .padding(14.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ComposableRemoteControl(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onOpen = onOpenRemoteControl
            )
            NfcAttack(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onOpenAttack = onOpenAttack,
                notificationCount = notificationCount
            )
        }
    }
}
