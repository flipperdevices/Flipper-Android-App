package com.flipperdevices.filemanager.search.impl.api

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.searchbar.ComposableSearchBar
import com.flipperdevices.filemanager.search.api.SearchDecomposeComponent
import com.flipperdevices.filemanager.search.impl.composable.ComposableFilesSearchScreen
import com.flipperdevices.filemanager.search.impl.viewmodel.SearchViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path

@ContributesAssistedFactory(AppGraph::class, SearchDecomposeComponent.Factory::class)
class SearchDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onFolderSelect: FolderSelectCallback,
    private val searchViewModelFactory: SearchViewModel.Factory
) : SearchDecomposeComponent(componentContext) {

    @Composable
    override fun Render() {
        val searchViewModel = viewModelWithFactory(path) {
            searchViewModelFactory.invoke(path)
        }
        val rootSearchViewModel = path.root
            .takeIf { !path.isRoot }
            ?.let { rootPath ->
                viewModelWithFactory(rootPath) {
                    searchViewModelFactory.invoke(rootPath)
                }
            }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val queryState by searchViewModel.queryState.collectAsState()
                ComposableSearchBar(
                    hint = "",
                    text = queryState.query,
                    onChangeText = {
                        searchViewModel.onQueryChange(it)
                        rootSearchViewModel?.onQueryChange(it)
                    },
                    onBack = onBack::invoke
                )
            }
        ) { contentPadding ->
            ComposableFilesSearchScreen(
                searchViewModel = searchViewModel,
                rootSearchViewModel = rootSearchViewModel,
                onFolderSelect = onFolderSelect::invoke,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}
