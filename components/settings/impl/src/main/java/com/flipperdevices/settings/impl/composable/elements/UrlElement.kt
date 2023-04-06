package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun UrlElement(
    @DrawableRes iconId: Int,
    @StringRes titleId: Int,
    url: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = modifier.clickableRipple {
            uriHandler.openUri(url)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                .size(18.dp),
            painter = painterResource(iconId),
            contentDescription = stringResource(titleId),
            tint = LocalPallet.current.iconTint100
        )
        SimpleElement(
            modifier = Modifier.weight(weight = 1f),
            titleId = titleId,
            paddings = PaddingValues(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 12.dp),
            titleTextStyle = LocalTypography.current.bodyR14.copy(
                textDecoration = TextDecoration.Underline
            )
        )
    }
}
