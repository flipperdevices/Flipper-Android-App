package com.flipperdevices.updater.ui.composable

import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.shared.R as SharedInfoResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.updater.ui.R
import com.flipperdevices.updater.ui.model.UpdaterScreenState
import com.flipperdevices.updater.ui.viewmodel.UpdaterViewModel

@Composable
fun ComposableUpdaterScreen(
    updaterScreenState: UpdaterScreenState,
    updaterViewModel: UpdaterViewModel
) {
    Column {
        Column(
            Modifier.weight(weight = 1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UpdaterScreenHeader()
            ComposableUpdateContent(updaterScreenState)
        }
        CancelButton(updaterScreenState, updaterViewModel)
    }
}

@Composable
private fun UpdaterScreenHeader() {
    Text(
        modifier = Modifier.padding(top = 48.dp, start = 14.dp, end = 14.dp),
        text = stringResource(R.string.update_screen_title),
        fontSize = 18.sp,
        fontWeight = FontWeight.W700,
        color = colorResource(DesignSystem.color.black_100),
        textAlign = TextAlign.Center
    )

    Image(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, start = 14.dp, end = 14.dp, bottom = 64.dp),
        painter = painterResource(SharedInfoResources.drawable.ic_white_flipper),
        contentDescription = null,
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun CancelButton(
    updaterScreenState: UpdaterScreenState,
    updaterViewModel: UpdaterViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (updaterScreenState == UpdaterScreenState.CancelingUpdate) {
            CircularProgressIndicator(
                color = colorResource(DesignSystem.color.accent_secondary)
            )
            return@Box
        }
        Text(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = updaterViewModel::cancel
            ),
            text = stringResource(R.string.update_screen_cancel),
            textAlign = TextAlign.Center,
            color = colorResource(DesignSystem.color.accent_secondary),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500
        )
    }
}
