package com.flipperdevices.remotecontrols.impl.grid.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
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
                actions = {
                    Row(modifier = Modifier) {
                        Text(
                            text = "Save",
                            color = LocalPalletV2.current.text.title.blackOnColor,
                            style = LocalTypography.current.titleEB18,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickableRipple(onClick = gridComponent::save)
                        )
//                        Icon(
//                            modifier = Modifier
//                                .size(24.dp)
//                                .clickableRipple(bounded = false, onClick = {}),
//                            painter = painterResource(R.drawable.ic_close),
//                            contentDescription = null,
//                            tint = LocalPalletV2.current.icon.blackAndWhite.blackOnColor
//                        )
                    }
                }
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
