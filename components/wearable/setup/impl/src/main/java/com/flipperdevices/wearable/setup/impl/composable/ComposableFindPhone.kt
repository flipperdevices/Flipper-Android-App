package com.flipperdevices.wearable.setup.impl.composable

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.wearable.core.ui.components.ComposableFlipperButton
import com.flipperdevices.wearable.setup.impl.R
import com.flipperdevices.wearable.setup.impl.model.FindPhoneModel
import com.flipperdevices.wearable.setup.impl.viewmodel.FindPhoneViewModel
import com.google.android.horologist.compose.layout.fillMaxRectangle
import kotlinx.coroutines.launch

@Composable
fun ComposableFindPhone() {
    val findPhoneViewModel = viewModel<FindPhoneViewModel>()

    val columnScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .padding()
            .verticalScroll(columnScrollState)
            .fillMaxRectangle()
            .onRotaryScrollEvent {
                info { "onRotaryScrollEvent" }
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
            FindPhoneModel.Loading -> ComposableFindPhoneLoading()
            is FindPhoneModel.Founded -> ComposableFoundedPhone(localFindPhoneModel.phoneName)
            FindPhoneModel.NotFound -> ComposableNotFoundedPhone()
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun ComposableFoundedPhone(name: String) {
    Text(
        text = name,
        style = LocalTypography.current.bodyM14
    )
}

@Composable
private fun ComposableNotFoundedPhone() {
    Text(
        text = stringResource(id = R.string.phone_missing),
        style = LocalTypography.current.bodyM14
    )
    ComposableFlipperButton(
        modifier = Modifier.padding(vertical = 16.dp),
        text = stringResource(R.string.install_app)
    )
}

@Composable
private fun ComposableFindPhoneLoading() {
    CircularProgressIndicator()
}
