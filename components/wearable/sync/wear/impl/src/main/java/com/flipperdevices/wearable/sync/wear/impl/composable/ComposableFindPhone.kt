package com.flipperdevices.wearable.sync.wear.impl.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.wearable.core.ui.components.ComposableFlipperButton
import com.flipperdevices.wearable.core.ui.components.ComposableWearOsScrollableColumn
import com.flipperdevices.wearable.sync.wear.impl.R

@Composable
fun ComposableFindPhone(
    onInstall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ComposableWearOsScrollableColumn(modifier) {
        Text(
            text = stringResource(id = R.string.phone_missing),
            style = LocalTypography.current.bodyM14
        )
        ComposableFlipperButton(
            modifier = Modifier.padding(all = 16.dp),
            textPadding = PaddingValues(
                vertical = 12.dp,
                horizontal = 20.dp
            ),
            text = stringResource(R.string.install_app),
            onClick = onInstall
        )
    }
}
