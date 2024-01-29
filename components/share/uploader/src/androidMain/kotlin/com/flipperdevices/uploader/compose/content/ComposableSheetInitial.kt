package com.flipperdevices.uploader.compose.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
internal fun ComposableSheetInitial() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 42.dp, bottom = 64.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ComposableSheetActionLoading()
        ComposableSheetActionLoading()
    }
}

@Composable
private fun ComposableSheetActionLoading() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(LocalPallet.current.shareSheetBackgroundAction.copy(alpha = 0.1f))
                .placeholderConnecting()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .height(16.dp)
                .width(80.dp)
                .placeholderConnecting()
        )
        Box(modifier = Modifier.height(12.dp))
    }
}
