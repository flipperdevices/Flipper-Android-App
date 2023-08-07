package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.firstpair.impl.model.SearchingState

@Composable
fun ComposableSearchingStatus(
    state: SearchingState,
    onHelpClicking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 50.dp)
            .defaultMinSize(minHeight = 42.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(weight = 1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.showSearching) {
                ComposableSearchingProgress()
            }
        }
        if (state.showHelp) {
            ComposableHelpButton(onHelpClicking)
        }
    }
}

@Composable
private fun RowScope.ComposableSearchingProgress() {
    Text(
        modifier = Modifier.padding(
            end = 8.dp,
            top = 8.dp,
            bottom = 8.dp,
            start = 18.dp
        ),
        text = stringResource(R.string.firstpair_search_title_status_text),
        style = LocalTypography.current.titleM18
    )
    CircularProgressIndicator(
        modifier = Modifier.size(size = 20.dp),
        strokeWidth = 2.dp,
        color = LocalPallet.current.progressBarGray
    )
}

@Composable
private fun ComposableHelpButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(end = 10.dp)
            .clickableRipple(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = stringResource(R.string.firstpair_search_title_help),
            color = LocalPallet.current.text30,
            style = LocalTypography.current.buttonM16
        )
        Icon(
            modifier = Modifier
                .size(height = 24.dp, width = 32.dp) // 24 (width) + 8 (padding) = 32
                .padding(end = 8.dp),
            painter = painterResource(R.drawable.ic_help),
            contentDescription = stringResource(R.string.firstpair_search_title_help),
            tint = LocalPallet.current.iconTint30
        )
    }
}
