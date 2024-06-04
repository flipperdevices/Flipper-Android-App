package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.updater.model.UpdateRequest

@Composable
fun ComposableWhatsNew(update: UpdateRequest) {
    if (update.changelog == null) {
        return
    }
    LocalPallet.current.text100
    Row {

    }
}