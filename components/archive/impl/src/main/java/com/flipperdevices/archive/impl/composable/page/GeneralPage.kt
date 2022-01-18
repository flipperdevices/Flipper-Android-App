package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.viewmodel.GeneralTabViewModel
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun GeneralPage(tabViewModel: GeneralTabViewModel = viewModel()) {
    val keys by tabViewModel.getKeys().collectAsState()
    val favoriteKeys by tabViewModel.getFavoriteKeys().collectAsState()

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
            FavoriteList(favoriteKeys) {
                tabViewModel.onKeyClick(it)
            }
            AllList(favoriteKeys, keys) {
                tabViewModel.onKeyClick(it)
            }
        }
    }
}

@SuppressWarnings("FunctionNaming")
private fun LazyListScope.FavoriteList(
    keys: List<FlipperKey>,
    onKeyClick: (FlipperKey) -> Unit
) {
    if (keys.isEmpty()) {
        return
    }
    item {
        Text(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 14.dp),
            text = stringResource(R.string.archive_tab_general_favorite_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }

    KeysList(keys, onKeyClick)
}

@SuppressWarnings("FunctionNaming")
private fun LazyListScope.AllList(
    favoriteKeys: List<FlipperKey>,
    keys: List<FlipperKey>?,
    onKeyClick: (FlipperKey) -> Unit
) {
    if (favoriteKeys.isNotEmpty()) {
        item {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                text = stringResource(R.string.archive_tab_general_all_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    KeysList(keys) {
        onKeyClick(it)
    }
}
