package com.flipperdevices.fmsearch.impl.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactoryWithoutRemember
import com.flipperdevices.core.ui.searchbar.ComposableSearchBar
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.fmsearch.api.FMSearchDecomposeComponent
import com.flipperdevices.fmsearch.impl.R
import com.flipperdevices.fmsearch.impl.composable.SearchItemComposable
import com.flipperdevices.fmsearch.impl.viewmodel.FMSearchViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FMSearchDecomposeComponent.Factory::class)
class FMSearchDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBackParameter: DecomposeOnBackParameter,
    @Assisted private val path: String,
    private val searchViewModelFactory: FMSearchViewModel.Factory
) : FMSearchDecomposeComponent(componentContext) {
    private val searchViewModel = viewModelWithFactoryWithoutRemember(path) {
        searchViewModelFactory(path)
    }

    @Composable
    override fun Render() {
        Column {
            ComposableSearchBar(
                hint = stringResource(R.string.fm_search_hint),
                onBack = onBackParameter::invoke,
                onChangeText = searchViewModel::search
            )
            val result by searchViewModel.getSearchResult().collectAsState()
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                items(result.items) { item ->
                    SearchItemComposable(
                        modifier = Modifier.fillMaxWidth(),
                        searchItem = item
                    )
                }
                if (result.inProgress) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = LocalPallet.current.accentSecond)
                        }
                    }
                }
            }
        }
    }
}
