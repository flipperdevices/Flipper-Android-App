package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.firstpair.impl.composable.common.ComposableSearchingAppBar
import com.flipperdevices.firstpair.impl.model.SearchingContent
import com.flipperdevices.firstpair.impl.model.SearchingState

@Composable
fun ComposableSearchingScreen(
    state: SearchingState,
    onBack: () -> Unit,
    onHelpClicking: () -> Unit,
    onSkipConnection: () -> Unit,
    onDeviceClick: (DiscoveredBluetoothDevice) -> Unit,
    onRefreshSearching: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ComposableSearchingAppBar(stringResource(R.string.firstpair_search_title), onBack)
        ComposableSearchingStatus(state, onHelpClicking)
        ComposableSearchingContent(
            modifier = Modifier.weight(weight = 1f),
            content = state.content,
            onDeviceClick = onDeviceClick,
            onRefreshSearching = onRefreshSearching
        )
        ComposableSearchingFooter(onSkipConnection)
    }
}

@Composable
fun ComposableSearchingContent(
    content: SearchingContent,
    onDeviceClick: (DiscoveredBluetoothDevice) -> Unit,
    modifier: Modifier = Modifier,
    onRefreshSearching: () -> Unit
) {
    when (content) {
        is SearchingContent.Finished -> Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.firstpair_search_title),
            textAlign = TextAlign.Center
        )
        is SearchingContent.FoundedDevices -> ComposableSearchingDevices(
            modifier = modifier,
            state = content,
            onDeviceClick = onDeviceClick,
            onRefreshSearching = onRefreshSearching
        )
        is SearchingContent.PermissionRequest -> ComposablePermissionRequest(
            modifier = modifier,
            state = content
        )
        SearchingContent.Searching -> ComposableSearchingProgress(modifier)
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableSearchingScreenPreview() {
    ComposableSearchingScreen(
        SearchingState(
            showSearching = true,
            showHelp = true,
            content = SearchingContent.Searching
        ),
        onBack = {},
        onHelpClicking = {},
        onSkipConnection = {},
        onRefreshSearching = {},
        onDeviceClick = {}
    )
}
