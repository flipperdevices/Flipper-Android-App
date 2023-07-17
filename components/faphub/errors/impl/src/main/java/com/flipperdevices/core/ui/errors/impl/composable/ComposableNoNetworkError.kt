package com.flipperdevices.core.ui.errors.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.errors.R
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableNoNetworkError(
    fapErrorSize: FapErrorSize,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    ComposableBaseError(
        modifier = modifier,
        titleId = R.string.common_error_no_network_title,
        descriptionId = R.string.common_error_no_network_desc,
        iconId = DesignSystem.drawable.ic_no_internet,
        darkIconId = DesignSystem.drawable.ic_no_internet_dark,
        onRetry = onRetry,
        fapErrorSize = fapErrorSize
    )
}

@Preview(
    showBackground = true
)
@Composable
private fun ComposableNoNetworkErrorPreview() {
    FlipperThemeInternal {
        ComposableNoNetworkError(onRetry = {}, fapErrorSize = FapErrorSize.FULLSCREEN)
    }
}
