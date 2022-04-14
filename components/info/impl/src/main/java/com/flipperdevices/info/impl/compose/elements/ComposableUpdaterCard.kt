package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.info.impl.R
import com.flipperdevices.updater.api.UpdaterUIApi

@Composable
fun ComposableUpdaterCard(
    modifier: Modifier,
    updaterUiApi: UpdaterUIApi
) {
    InfoElementCard(
        modifier = modifier,
        titleId = R.string.info_device_update_title
    ) {
        updaterUiApi.ComposableUpdateCardContent()
    }
}
