package com.flipperdevices.remotecontrols.impl.grid.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.impl.grid.composable.components.GridComposableContent
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent

@Composable
fun GridComposable(
    gridComponent: GridComponent,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val model by remember(gridComponent, coroutineScope) {
        gridComponent.model(coroutineScope)
    }.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            SharedTopBar(
                onBackClick = gridComponent::pop,
            )
        },
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body,
        scaffoldState = scaffoldState,
        content = { scaffoldPaddings ->
            GridComposableContent(
                gridComponent = gridComponent,
                model = model,
                modifier = Modifier.padding(scaffoldPaddings)
            )
        }
    )
}
