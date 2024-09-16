package com.flipperdevices.toolstab.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.toolstab.impl.R
import com.flipperdevices.toolstab.impl.composable.elements.MifareClassicComposable
import com.flipperdevices.toolstab.impl.composable.elements.RemoteControlsComposable

@Composable
fun ComposableHub(
    hasNotification: Boolean,
    showRemoteControls: Boolean,
    onOpenMfKey32: () -> Unit,
    onOpenRemoteControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        OrangeAppBar(
            titleId = R.string.toolstab_title
        )
        MifareClassicComposable(
            hasMfKey32Notification = hasNotification,
            onOpenMfKey32 = onOpenMfKey32
        )
        if (showRemoteControls) {
            RemoteControlsComposable(
                onOpenRemoteControls = onOpenRemoteControls
            )
        }
    }
}
