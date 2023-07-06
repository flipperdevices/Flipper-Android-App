package com.flipperdevices.core.ui.errors.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.errors.R
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ComposableBaseError(
    @StringRes titleId: Int,
    @StringRes descriptionId: Int,
    @DrawableRes iconId: Int,
    modifier: Modifier = Modifier,
    @DrawableRes darkIconId: Int = iconId,
    onRetry: (() -> Unit)? = null
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Image(
        painter = painterResource(
            if (MaterialTheme.colors.isLight) {
                iconId
            } else {
                darkIconId
            }
        ),
        contentDescription = stringResource(titleId)
    )

    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = stringResource(titleId),
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text100,
        textAlign = TextAlign.Center
    )
    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = stringResource(descriptionId),
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text40,
        textAlign = TextAlign.Center
    )

    if (onRetry != null) {
        Text(
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable(onClick = onRetry),
            text = stringResource(R.string.common_error_btn),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.accentSecond,
            textAlign = TextAlign.Center
        )
    }
}
