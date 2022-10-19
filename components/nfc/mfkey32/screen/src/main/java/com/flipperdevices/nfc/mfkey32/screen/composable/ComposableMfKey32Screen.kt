package com.flipperdevices.nfc.mfkey32.screen.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.nfc.mfkey32.screen.viewmodel.MfKey32ViewModel
import tangle.viewmodel.compose.tangleViewModel

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableMfKey32Screen() {
    val viewModel = tangleViewModel<MfKey32ViewModel>()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = viewModel::runNfcTool) {
            Text("Test NFC tool")
        }
    }
}
