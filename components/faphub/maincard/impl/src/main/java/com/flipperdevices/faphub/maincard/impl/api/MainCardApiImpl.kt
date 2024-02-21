package com.flipperdevices.faphub.maincard.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
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
    @Suppress("NonSkippableComposable")
    override fun ComposableMainCard(
        modifier: Modifier,
        componentContext: ComponentContext,
        onClick: () -> Unit
    ) {
        val viewModel = componentContext.viewModelWithFactory(key = null) {
            fapMainCardViewModelProvider.get()
        }
        val state by viewModel.getFapMainCardState().collectAsState()
        val isExistAppReadyToUpdate by viewModel.isExistAppReadyToUpdateState().collectAsState()
        ComposableMainCardInternal(
            modifier = modifier,
            onClick = onClick,
            state = state,
            isExistAppReadyToUpdate = isExistAppReadyToUpdate
        )
    }
}
