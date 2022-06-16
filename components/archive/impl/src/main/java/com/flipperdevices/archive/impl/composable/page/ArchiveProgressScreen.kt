package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ArchiveProgressScreen(inProgressState: SynchronizationState.InProgress) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = colorResource(DesignSystem.color.accent_secondary),
            strokeWidth = 2.dp
        )
        Text(
            modifier = Modifier.padding(top = 14.dp),
            text = LocalContext.current.getString(
                R.string.archive_sync_percent,
                inProgressState.progress.roundPercentToString()
            ),
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_60),
            textAlign = TextAlign.Center
        )
    }
}
