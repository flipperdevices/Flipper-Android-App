package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.markdown.annotatedStringFromMarkdown
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.firstpair.impl.model.SearchingContent

@Composable
fun ComposablePermissionRequest(
    state: SearchingContent.PermissionRequest,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 62.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResourceByKey(state.image),
            contentDescription = stringResource(state.title)
        )
        Text(
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 12.dp
            ),
            text = stringResource(state.title),
            style = LocalTypography.current.buttonM16,
            textAlign = TextAlign.Center
        )
        Text(
            text = annotatedStringFromMarkdown(state.description),
            style = LocalTypography.current.bodyR14,
            color = LocalPallet.current.text40,
            textAlign = TextAlign.Center
        )
        ComposableFlipperButton(
            modifier = Modifier.padding(all = 24.dp),
            text = stringResource(state.buttonText),
            textStyle = TextStyle(fontSize = 14.sp),
            onClick = { state.onButtonClick() }
        )
    }
}
