package com.flipperdevices.firstpair.impl.composable.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.firstpair.impl.composable.searching.ComposableSearchingAppBar

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableHelp(
    onBack: () -> Unit = {}
) {
    Column() {
        ComposableSearchingAppBar(stringResource(R.string.firstpair_help_title), onBack)
        ComposableHelpList()
    }
}

@Composable
fun ComposableHelpList() {
    val list = HelpOptions.values
    LazyColumn(
        modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(space = 24.dp)
    ) {
        itemsIndexed(list) { index, item ->
            ComposableHelpItem(index, item)
        }
    }
}
