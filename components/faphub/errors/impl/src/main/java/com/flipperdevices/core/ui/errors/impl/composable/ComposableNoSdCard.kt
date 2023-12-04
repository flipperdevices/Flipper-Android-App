package com.flipperdevices.core.ui.errors.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.errors.R
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableNoSdCard(
    fapErrorSize: FapErrorSize,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    ComposableBaseError(
        modifier = modifier,
        titleId = R.string.common_error_no_sd_card_title,
        descriptionId = R.string.common_error_no_sd_card_desc,
        iconId = DesignSystem.drawable.ic_no_sd,
        darkIconId = DesignSystem.drawable.ic_no_sd_dark,
        onRetry = onRetry,
        fapErrorSize = fapErrorSize
    )
}
