package com.flipperdevices.core.ui.errors.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.errors.R
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Composable
internal fun ComposableFlipperNotConnectedError(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    ComposableBaseError(
        modifier = modifier,
        titleId = R.string.common_error_not_connected_flipper_apps_title,
        descriptionId = R.string.common_error_not_connected_flipper_apps_desc,
        iconId = R.drawable.ic_flipper_not_connected,
        onRetry = onRetry
    )
}

@Preview(
    showBackground = true
)
@Composable
private fun ComposableFlipperNotConnectedErrorPreview() {
    FlipperThemeInternal {
        ComposableFlipperNotConnectedError(onRetry = {})
    }
}
