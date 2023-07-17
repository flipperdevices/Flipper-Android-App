package com.flipperdevices.core.ui.errors.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.errors.R
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.faphub.errors.api.FapErrorSize

@Composable
internal fun ComposableWrongRequestError(
    fapErrorSize: FapErrorSize,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    ComposableBaseError(
        modifier = modifier,
        titleId = R.string.common_error_wrong_request_title,
        descriptionId = R.string.common_error_wrong_request_desc,
        iconId = R.drawable.ic_update_app,
        onRetry = onRetry,
        fapErrorSize = fapErrorSize
    )
}

@Preview(
    showBackground = true
)
@Composable
private fun ComposableWrongRequestErrorPreview() {
    FlipperThemeInternal {
        ComposableWrongRequestError(onRetry = {}, fapErrorSize = FapErrorSize.FULLSCREEN)
    }
}
