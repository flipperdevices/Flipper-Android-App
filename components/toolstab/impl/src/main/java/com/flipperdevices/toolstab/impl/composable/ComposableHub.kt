package com.flipperdevices.toolstab.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.toolstab.impl.R
import com.flipperdevices.toolstab.impl.composable.elements.NfcAttack

@Composable
fun ComposableHub(
    notificationCount: Int,
    onOpenAttack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        OrangeAppBar(
            titleId = R.string.toolstab_title
        )
        NfcAttack(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            onOpenAttack = onOpenAttack,
            notificationCount = notificationCount
        )
    }
}
