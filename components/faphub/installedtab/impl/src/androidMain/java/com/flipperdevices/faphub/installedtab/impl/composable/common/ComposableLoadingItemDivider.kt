package com.flipperdevices.faphub.installedtab.impl.composable.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposableLoadingItemDivider(modifier: Modifier = Modifier) = Divider(
    modifier = modifier
        .fillMaxWidth(),
    thickness = 1.dp,
    color = LocalPallet.current.fapHubDividerColor
)
