package com.flipperdevices.nfc.mfkey32.screen.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.nfc.mfkey32.screen.composable.output.AllKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.output.DuplicatedKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.output.UniqueKeys
import com.flipperdevices.nfc.mfkey32.screen.composable.progressbar.ComposableMfKey32Progress
import com.flipperdevices.nfc.mfkey32.screen.viewmodel.MfKey32ViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableMfKey32Screen(navController: NavController) {
    val viewModel = tangleViewModel<MfKey32ViewModel>()
    val state by viewModel.getMfKey32State().collectAsState()
    val foundedKeys by viewModel.getFoundedInformation().collectAsState()
    Column {
        OrangeAppBar(
            titleId = R.string.mfkey32_title,
            onBack = navController::popBackStack
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ComposableMfKey32Progress(navController, state)
            }
            if (foundedKeys.keys.isNotEmpty()) {
                AllKeys(foundedKeys.keys)
            }
            if (foundedKeys.uniqueKeys.isNotEmpty()) {
                UniqueKeys(foundedKeys.uniqueKeys)
            }
            if (foundedKeys.duplicated.isNotEmpty()) {
                DuplicatedKeys(foundedKeys.duplicated)
            }
        }
    }
}
