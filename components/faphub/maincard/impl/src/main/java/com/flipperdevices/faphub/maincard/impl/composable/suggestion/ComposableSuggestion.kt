package com.flipperdevices.faphub.maincard.impl.composable.suggestion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.SmallHorizontalAppCard
import com.flipperdevices.faphub.maincard.impl.R
import com.flipperdevices.faphub.maincard.impl.model.FapMainCardState
import com.flipperdevices.faphub.maincard.impl.viewmodel.FapMainCardViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableSuggestion(modifier: Modifier = Modifier) {
    val viewModel = tangleViewModel<FapMainCardViewModel>()
    val state by viewModel.getFapMainCardState().collectAsState()
    ComposableSuggestionContent(state, modifier)
}

@Composable
private fun ComposableSuggestionContent(
    state: FapMainCardState,
    modifier: Modifier = Modifier
) {
    when (state) {
        FapMainCardState.FailedLoad -> Text(
            modifier = modifier
                .defaultMinSize(42.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.maincard_fallback_text),
            style = LocalTypography.current.bodyR14,
            color = LocalPallet.current.text30
        )
        is FapMainCardState.Loaded -> SmallHorizontalAppCard(
            modifier = modifier,
            fapItem = state.fapItem
        )
        FapMainCardState.Loading -> Box(
            modifier = modifier
                .height(42.dp)
                .fillMaxWidth()
                .placeholderConnecting()
        )
    }
}
