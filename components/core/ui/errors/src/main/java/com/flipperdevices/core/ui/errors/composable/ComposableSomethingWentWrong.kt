package com.flipperdevices.core.ui.errors.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.errors.R
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Composable
internal fun ComposableGeneralError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComposableBaseError(
        modifier = modifier,
        titleId = R.string.common_error_general_title,
        descriptionId = R.string.common_error_general_desc,
        iconId = R.drawable.ic_general_error,
        onRetry = onRetry
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableGeneralErrorPreview() {
    FlipperThemeInternal {
        ComposableGeneralError(onRetry = {})
    }
}
