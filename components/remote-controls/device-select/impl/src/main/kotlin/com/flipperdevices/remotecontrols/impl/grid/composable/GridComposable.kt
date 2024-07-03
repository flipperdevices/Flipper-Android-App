package com.flipperdevices.remotecontrols.impl.grid.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.remotecontrols.impl.categories.composable.components.SharedTopBar
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent

@Composable
internal fun GridComposable(gridComponent: GridComponent) {
    val model by gridComponent.model.collectAsState()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        topBar = {
            SharedTopBar(
                title = "",
                subtitle = "",
                onBackClicked = gridComponent::pop
            )
        },
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body,
        scaffoldState = scaffoldState,
        content = { scaffoldPaddings ->
            Crossfade(
                targetState = model,
                modifier = Modifier.padding(scaffoldPaddings)
            ) { model ->
                when (model) {
                    GridComponent.Model.Error -> {
                        Text(
                            text = "Error",
                            style = androidx.compose.material.MaterialTheme.typography.subtitle2,
                            color = LocalPalletV2.current.text.title.blackOnColor
                        )
                    }

                    is GridComponent.Model.Loaded -> {
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart,
                            content = {
                                ButtonsComposable(
                                    pageLayout = model.pagesLayout.pages.first(),
                                    onButtonClicked = { button, keyIdentifier ->
                                        gridComponent.onButtonClicked(keyIdentifier)
                                    },
                                )
                            }
                        )
                    }

                    GridComponent.Model.Loading -> {
                        LoadingView()
                    }
                }
            }
        }
    )
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
