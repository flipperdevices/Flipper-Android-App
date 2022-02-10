package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.ComposableFlipperButton
import com.flipperdevices.firstpair.impl.model.SearchingContent

@Composable
fun ComposablePermissionRequest(modifier: Modifier, state: SearchingContent.PermissionRequest) {
    Column(
        modifier = modifier.padding(horizontal = 62.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(state.image),
            contentDescription = stringResource(state.title)
        )
        Text(
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 12.dp
            ),
            text = stringResource(state.title),
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            color = colorResource(DesignSystem.color.black_100),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(state.description),
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_40),
            textAlign = TextAlign.Center
        )
        ComposableFlipperButton(
            modifier = Modifier.padding(all = 24.dp),
            text = stringResource(state.buttonText),
            fontSize = 14.sp,
            onClick = { state.onButtonClick() }
        )
    }
}
