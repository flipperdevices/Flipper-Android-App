package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.search.R
import com.flipperdevices.archive.search.model.SearchState
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.archive.shared.composable.ComposableKeyCard
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.LocalRouter

@Composable
fun ComposableSearchContent(modifier: Modifier, searchViewModel: SearchViewModel) {
    val state by searchViewModel.getState().collectAsState()
    val localState = state
    when (localState) {
        SearchState.Loading -> CategoryLoadingProgress(modifier)
        is SearchState.Loaded -> if (localState.keys.isEmpty()) {
            CategoryEmpty(modifier)
        } else CategoryList(modifier, searchViewModel, localState.keys)
    }
}

@Composable
private fun CategoryList(
    modifier: Modifier,
    searchViewModel: SearchViewModel,
    keys: List<Pair<FlipperKeyParsed, FlipperKeyPath>>
) {
    val router = LocalRouter.current
    LazyColumn(
        modifier.padding(top = 14.dp)
    ) {
        items(keys) { (flipperKeyParsed, keyPath) ->
            ComposableKeyCard(Modifier.padding(bottom = 14.dp), flipperKeyParsed) {
                searchViewModel.openKeyScreen(router, keyPath)
            }
        }
    }
}

@Composable
private fun CategoryLoadingProgress(modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CategoryEmpty(modifier: Modifier) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_not_found),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(R.string.search_not_found_title),
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            color = colorResource(DesignSystem.color.black_100)
        )
        Text(
            modifier = Modifier.padding(top = 12.dp, start = 98.dp, end = 98.dp),
            text = stringResource(R.string.search_not_found_description),
            color = colorResource(DesignSystem.color.black_40),
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
    }
}
