package com.flipperdevices.core.ui.errors.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.errors.R
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Composable
internal fun ComposableNoServerError(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    ComposableBaseError(
        modifier = modifier,
        titleId = R.string.common_error_no_server_title,
        descriptionId = R.string.common_error_no_server_desc,
        iconId = R.drawable.ic_no_server,
        onRetry = onRetry
    )
}

@Preview(
    showBackground = true
)
@Composable
private fun ComposableNoNetworkErrorPreview() {
    FlipperThemeInternal {
        ComposableNoServerError(onRetry = {})
    }
}
