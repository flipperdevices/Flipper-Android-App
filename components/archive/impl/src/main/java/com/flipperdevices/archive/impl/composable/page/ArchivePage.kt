package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.impl.model.ArchiveTab
import com.flipperdevices.archive.impl.viewmodel.TabViewModel
import com.flipperdevices.archive.impl.viewmodel.TabViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun ArchivePage(
    tab: ArchiveTab.Specified,
    tabViewModel: TabViewModel = viewModel(
        key = tab.fileType.humanReadableName,
        factory = TabViewModelFactory(tab)
    )
) {
    val keys by tabViewModel.getKeys().collectAsState()
    SwipeRefresh(
        state = rememberSwipeRefreshState(false),
        onRefresh = { tabViewModel.refresh() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp)
        ) {
            KeysList(keys)
        }
    }
}
