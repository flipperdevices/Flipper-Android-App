package com.flipperdevices.remotecontrols.impl.grid.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.LoadingComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.ifrmvp.model.IfrButton
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.grid.impl.R as GridR

@Composable
internal fun LoadedContent(
    pagesLayout: PagesLayout,
    onButtonClicked: (IfrButton, IfrKeyIdentifier) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
        content = {
            ButtonsComposable(
                pageLayout = pagesLayout.pages.firstOrNull(),
                onButtonClicked = onButtonClicked,
                onReload = onReload
            )
        }
    )
}

@Composable
fun GridComposable(
    gridComponent: GridComponent,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val model by remember(gridComponent, coroutineScope) {
        gridComponent.model(coroutineScope)
    }.collectAsState()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        modifier = modifier,
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
                        ErrorComposable(
                            desc = stringResource(GridR.string.empty_page),
                            onReload = gridComponent::tryLoad
                        )
                    }

                    is GridComponent.Model.Loaded -> {
                        LoadedContent(
                            pagesLayout = model.pagesLayout,
                            onButtonClicked = { button, keyIdentifier ->
                                gridComponent.onButtonClicked(keyIdentifier)
                            },
                            onReload = gridComponent::tryLoad
                        )
                    }

                    is GridComponent.Model.Loading -> {
                        LoadingComposable(progress = model.progress)
                    }
                }
            }
        }
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun LoadedContentEmptyPreview() {
    FlipperThemeInternal {
        LoadedContent(
            pagesLayout = PagesLayout(emptyList()),
            onButtonClicked = { _, _ -> },
            onReload = {}
        )
    }
}
