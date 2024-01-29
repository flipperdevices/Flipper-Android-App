package com.flipperdevices.faphub.search.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.search.impl.R

@Composable
fun ComposableResultEmpty(
    request: String,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Text(
        text = stringResource(R.string.faphub_search_empty_title),
        style = LocalTypography.current.bodyM14,
        textAlign = TextAlign.Center,
        color = LocalPallet.current.text100
    )
    Text(
        text = stringResource(R.string.faphub_search_empty_desc, request),
        style = LocalTypography.current.bodyM14,
        textAlign = TextAlign.Center,
        color = LocalPallet.current.text40
    )
}
