package com.flipperdevices.faphub.appcard.composable.paging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import flipperapp.components.faphub.appcard.composable.generated.resources.Res
import flipperapp.components.faphub.appcard.composable.generated.resources.faphub_catalog_no_apps_desc
import flipperapp.components.faphub.appcard.composable.generated.resources.faphub_catalog_no_apps_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComposableEmpty(
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Text(
        text = stringResource(Res.string.faphub_catalog_no_apps_title),
        style = LocalTypography.current.bodyM14,
        color = LocalPallet.current.text100,
        textAlign = TextAlign.Center
    )
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = stringResource(Res.string.faphub_catalog_no_apps_desc),
        style = LocalTypography.current.bodyM14,
        color = LocalPallet.current.text40,
        textAlign = TextAlign.Center
    )
}
