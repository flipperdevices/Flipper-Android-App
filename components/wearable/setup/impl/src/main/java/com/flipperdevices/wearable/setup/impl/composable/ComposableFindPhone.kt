package com.flipperdevices.wearable.setup.impl.composable

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.wearable.core.ui.components.ComposableFlipperButton
import com.flipperdevices.wearable.setup.impl.R
import com.flipperdevices.wearable.setup.impl.model.FindPhoneState
import com.flipperdevices.wearable.setup.impl.viewmodel.FindPhoneViewModel
import com.google.android.horologist.compose.layout.fillMaxRectangle
import kotlinx.coroutines.launch

@Composable
fun ComposableFindPhone(
    onFoundPhone: () -> Unit,
    modifier: Modifier = Modifier,
    findPhoneViewModel: FindPhoneViewModel = viewModel()
) {
    val columnScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .verticalScroll(columnScrollState)
            .fillMaxRectangle()
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    columnScrollState.scrollBy(it.verticalScrollPixels)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val findPhoneModel by findPhoneViewModel.getFindPhoneModelFlow().collectAsState()
        val localFindPhoneModel = findPhoneModel

        when (localFindPhoneModel) {
            FindPhoneState.Loading -> ComposableFindPhoneLoading()
            is FindPhoneState.Founded -> onFoundPhone()
            FindPhoneState.NotFound -> ComposableNotFoundedPhone(
                findPhoneViewModel::openStore,
                findPhoneViewModel::checkPhone
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun ColumnScope.ComposableNotFoundedPhone(onInstall: () -> Unit, onCheckAgain: () -> Unit) {
    Text(
        text = stringResource(id = R.string.phone_missing),
        style = LocalTypography.current.bodyM14
    )
    ComposableFlipperButton(
        modifier = Modifier
            .padding(
                top = 6.dp
            )
            .fillMaxWidth(),
        text = stringResource(R.string.install_app),
        onClick = onInstall,
        textPadding = PaddingValues(
            vertical = 3.dp,
            horizontal = 3.dp
        ),
        cornerRoundSize = 6.dp
    )
    Text(
        modifier = Modifier
            .padding(all = 4.dp)
            .clickableRipple(onClick = onCheckAgain),
        text = stringResource(id = R.string.check_again),
        style = LocalTypography.current.subtitleM10
    )
}

@Composable
private fun ComposableFindPhoneLoading() {
    CircularProgressIndicator()
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    device = Devices.WEAR_OS_LARGE_ROUND,
    fontScale = 2f
)
@Composable
private fun ComposableNotFoundedPhonePreview() {
    FlipperThemeInternal {
        val columnScrollState = rememberScrollState()

        Column(
            Modifier
                .fillMaxRectangle()
                .verticalScroll(columnScrollState),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComposableNotFoundedPhone(
                onInstall = {},
                onCheckAgain = {}
            )
        }
    }
}
