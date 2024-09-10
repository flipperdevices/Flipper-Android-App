package com.flipperdevices.ifrmvp.core.ui.layout.shared

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.ifrmvp.core.ui.button.ButtonItemComposable
import com.flipperdevices.ifrmvp.core.ui.layout.core.GridItemComposable
import com.flipperdevices.ifrmvp.core.ui.layout.core.LocalScaleFactor
import com.flipperdevices.ifrmvp.core.ui.layout.core.rememberScaleFactor
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants
import com.flipperdevices.ifrmvp.model.IfrButton
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PageLayout
import com.flipperdevices.remotecontrols.core.ui.R as RemoteControlsR

@Composable
internal fun BoxWithConstraintsScope.ButtonsComposable(
    pageLayout: PageLayout?,
    emulatedKeyIdentifier: IfrKeyIdentifier?,
    isSyncing: Boolean,
    isConnected: Boolean,
    onButtonClick: (IfrButton, IfrKeyIdentifier) -> Unit,
    modifier: Modifier = Modifier,
    onReload: (() -> Unit)? = null,
) {
    if (pageLayout?.buttons.isNullOrEmpty()) {
        ErrorComposable(
            desc = stringResource(RemoteControlsR.string.empty_page),
            onReload = onReload
        )
    }
    BoxWithConstraints(
        modifier = modifier
            .align(Alignment.Center)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val scaleFactor = rememberScaleFactor()
        CompositionLocalProvider(LocalScaleFactor provides scaleFactor) {
            BoxWithConstraints(
                modifier = Modifier
                    .width(GridConstants.SCALE_WIDTH.sf)
                    .height(GridConstants.SCALE_HEIGHT.sf),
            ) {
                pageLayout?.buttons
                    .orEmpty()
                    .forEach { button ->
                        GridItemComposable(
                            modifier = Modifier,
                            position = button.position,
                            content = {
                                ButtonItemComposable(
                                    buttonData = button.data,
                                    emulatedKeyIdentifier = emulatedKeyIdentifier,
                                    isSyncing = isSyncing,
                                    isConnected = isConnected,
                                    onKeyDataClick = { keyIdentifier ->
                                        onButtonClick.invoke(button, keyIdentifier)
                                    }
                                )
                            }
                        )
                    }
            }
        }
    }
}
