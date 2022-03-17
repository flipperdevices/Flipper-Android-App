package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.search.viewmodel.SearchViewModel

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableSearch(searchViewModel: SearchViewModel = viewModel()) {
    Column {
        ComposableSearchBar(searchViewModel)
        ComposableSearchContent(
            Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
            searchViewModel
        )
    }
}
