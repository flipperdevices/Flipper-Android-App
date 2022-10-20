package com.flipperdevices.nfc.mfkey32.screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalPallet.current.accent)
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 11.dp),
                text = stringResource(R.string.mfkey32_title),
                style = LocalTypography.current.titleB20,
                color = LocalPallet.current.onAppBar
            )
        }
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


