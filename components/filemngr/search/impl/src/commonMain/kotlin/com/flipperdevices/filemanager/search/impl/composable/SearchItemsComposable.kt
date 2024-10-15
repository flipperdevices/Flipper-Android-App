package com.flipperdevices.filemanager.search.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.filemanager.search.impl.viewmodel.SearchViewModel
import okio.Path

@Composable
fun SearchItemsComposable(
    currentDirState: SearchViewModel.State.Loaded,
    rootSearchState: SearchViewModel.State.Loaded?,
    onFolderSelect: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(14.dp),
    ) {
        item { ListingTitleComposable(currentDirState.path) }

        FolderCardListLazyComposable(
            searchState = currentDirState,
            onFolderSelect = onFolderSelect
        )
        NoFilesLazyComposable(currentDirState)

        if (currentDirState.isSearching) {
            FolderCardPlaceholderLazyComposable()
        }

        rootSearchState?.let { rootSearchState ->
            item { ListingTitleComposable(rootSearchState.path) }

            FolderCardListLazyComposable(
                searchState = rootSearchState,
                onFolderSelect = onFolderSelect
            )
            NoFilesLazyComposable(rootSearchState)

            if (rootSearchState.isSearching) {
                FolderCardPlaceholderLazyComposable()
            }
        }
    }
}
