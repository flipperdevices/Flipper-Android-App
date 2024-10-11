package com.flipperdevices.filemanager.search.impl.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.filemanager.search.impl.viewmodel.SearchViewModel
import okio.Path

@Composable
fun ComposableFilesSearchScreen(
    path: Path,
    searchViewModel: SearchViewModel,
    onFolderSelect: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchState by searchViewModel.state.collectAsState()

    Column(modifier = modifier) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = searchState,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            contentKey = { it::class.simpleName },
        ) { currentDirState ->
            when (currentDirState) {
                is SearchViewModel.State.Loaded -> {
                    SearchItemsComposable(
                        currentDirState = currentDirState,
                        onFolderSelect = onFolderSelect,
                        path = path
                    )
                }

                SearchViewModel.State.Loading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(14.dp),
                    ) {
                        FilesPlaceholderComposable()
                    }
                }

                SearchViewModel.State.Unsupported -> {
                    NoListingFeatureComposable()
                }
            }
        }
    }
}
