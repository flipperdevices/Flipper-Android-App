package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.composable.category.ComposableCategoryCard
import com.flipperdevices.archive.impl.model.CategoryItem
import com.flipperdevices.archive.impl.viewmodel.GeneralTabViewModel
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.R as DesignSystem

@Composable
fun GeneralPage(
    tabViewModel: GeneralTabViewModel = viewModel(),
    onCategoryPress: (CategoryItem) -> Unit,
    onDeletedPress: () -> Unit
) {
    val keys by tabViewModel.getKeys().collectAsState()
    val favoriteKeys by tabViewModel.getFavoriteKeys().collectAsState()
    val isKeysPresented = !favoriteKeys.isNullOrEmpty() || !keys.isNullOrEmpty()

    Column(verticalArrangement = Arrangement.Top) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                ComposableCategoryCard(onCategoryPress, onDeletedPress)
            }
            if (isKeysPresented) {
                KeyCatalog(favoriteKeys, keys)
            }
        }

        if (!isKeysPresented) {
            ComposableNoKeys()
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.KeyCatalog(
    favoriteKeys: List<FlipperKey>,
    otherKeys: List<FlipperKey>?
) {
    if (!favoriteKeys.isNullOrEmpty()) {
        item {
            ComposableFavoriteKeysTitle()
        }
        ComposableKeysGrid(favoriteKeys)
    }

    if (!otherKeys.isNullOrEmpty()) {
        item {
            ComposableAllKeysTitle()
        }
        ComposableKeysGrid(otherKeys)
    }
}

@Composable
private fun ColumnScope.ComposableNoKeys() {
    Box(
        modifier = Modifier
            .weight(weight = 1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.archive_content_empty),
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            color = colorResource(DesignSystem.color.black_40)
        )
    }
}

@Composable
private fun ColumnScope.ComposableProgress() {
    Row(
        modifier = Modifier
            .weight(weight = 1f)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_progress),
            contentDescription = null
        )
    }
}
