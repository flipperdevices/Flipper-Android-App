package com.flipperdevices.faphub.maincard.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.faphub.maincard.api.MainCardApi
import com.flipperdevices.faphub.maincard.impl.composable.ComposableMainCardInternal
import com.flipperdevices.faphub.maincard.impl.viewmodel.FapMainCardViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, MainCardApi::class)
class MainCardApiImpl @Inject constructor(
    private val fapMainCardViewModelProvider: Provider<FapMainCardViewModel>
) : MainCardApi {
    @Composable
    override fun ComposableMainCard(
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        val viewModel = viewModelWithFactory(key = null) {
            fapMainCardViewModelProvider.get()
        }
        val state by viewModel.getFapMainCardState().collectAsState()
        ComposableMainCardInternal(
            modifier = modifier,
            onClick = onClick,
            state = state
        )
    }
}
