package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.impl.composable.key.ComposableFlipperKey
import com.flipperdevices.archive.impl.model.ArchiveTab
import com.flipperdevices.archive.impl.viewmodel.TabViewModel
import com.flipperdevices.archive.impl.viewmodel.TabViewModelFactory

@Composable
fun ArchivePage(
    tab: ArchiveTab,
    tabViewModel: TabViewModel = viewModel(
        key = tab.fileType?.humanReadableName,
        factory = TabViewModelFactory(tab)
    )
) {
    val keys by tabViewModel.getKeys().collectAsState()

    if (keys.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 24.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp)
        ) {
            items(keys.size) {
                ComposableFlipperKey(keys[it])
            }
        }
    }
}
