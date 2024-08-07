package com.flipperdevices.remotecontrols.impl.grid.remote.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.impl.grid.remote.composable.components.RemoteGridComposableContent
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent

@Composable
fun RemoteGridComposable(
    remoteGridComponent: RemoteGridComponent,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val model by remember(remoteGridComponent, coroutineScope) {
        remoteGridComponent.model(coroutineScope)
    }.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            SharedTopBar(
                onBackClick = remoteGridComponent::pop,
                actions = {
                    AnimatedVisibility(model.isFilesSaved) {
                        Row(modifier = Modifier) {
                            Text(
                                text = "Save",
                                color = LocalPalletV2.current.text.title.blackOnColor,
                                style = LocalTypography.current.titleEB18,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.clickableRipple(onClick = remoteGridComponent::save)
                            )
                        }
                    }
                }
            )
        },
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body,
        scaffoldState = scaffoldState,
        content = { scaffoldPaddings ->
            RemoteGridComposableContent(
                remoteGridComponent = remoteGridComponent,
                model = model,
                modifier = Modifier.padding(scaffoldPaddings)
            )
        }
    )
}
