package com.flipperdevices.share.receive.composable

import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.shared.bar.ComposableBarCancelIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitle
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar
import com.flipperdevices.share.receive.R

@Composable
fun ComposableKeySaveBar(onBack: () -> Unit) {
    ComposableKeyScreenAppBar(
        centerBlock = { ComposableBarTitle(modifier = it, textId = R.string.receive_title) },
        endBlock = { ComposableBarCancelIcon(modifier = it, onClick = onBack) }
    )
}
