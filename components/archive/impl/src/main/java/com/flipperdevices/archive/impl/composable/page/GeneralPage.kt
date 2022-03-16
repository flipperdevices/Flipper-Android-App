package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    onKeyClick: (FlipperKey) -> Unit,
    onCategoryPress: (CategoryItem) -> Unit,
    onDeletedPress: () -> Unit
) {
    val keys by tabViewModel.getKeys().collectAsState()
    val favoriteKeys by tabViewModel.getFavoriteKeys().collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ComposableCategoryCard(onCategoryPress, onDeletedPress)

        if (!favoriteKeys.isNullOrEmpty() || !keys.isNullOrEmpty()) {
            KeyCatalog(favoriteKeys, keys)
        } else ComposableNoKeys()
    }
}

@Composable
private fun ColumnScope.KeyCatalog(favoriteKeys: List<FlipperKey>, otherKeys: List<FlipperKey>?) {
    if (!favoriteKeys.isNullOrEmpty()) {
        FavoritesKeysList(keys = favoriteKeys, onKeyClick = { })
    }

    if (!otherKeys.isNullOrEmpty()) {
        AllKeysList(keys = otherKeys, onKeyClick = { })
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
