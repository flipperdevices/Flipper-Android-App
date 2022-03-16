package com.flipperdevices.archive.impl.composable.category

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.impl.model.CategoryItem
import com.flipperdevices.archive.impl.viewmodel.CategoryViewModel
import com.flipperdevices.core.ui.R as DesignSystem

@Composable
fun ComposableCategoryCard(
    onCategoryPress: (CategoryItem) -> Unit,
    onDeletedPress: () -> Unit
) {
    Card(
        modifier = Modifier.padding(top = 14.dp, start = 14.dp, end = 14.dp, bottom = 2.dp),
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        ComposableCategoryList(onCategoryPress = onCategoryPress, onDeletedPress = onDeletedPress)
    }
}

@Composable
private fun ComposableCategoryList(
    categoryViewModel: CategoryViewModel = viewModel(),
    onCategoryPress: (CategoryItem) -> Unit,
    onDeletedPress: () -> Unit
) {
    val categories by categoryViewModel.getCategoriesFlow().collectAsState()
    val deletedCategory by categoryViewModel.getDeletedFlow().collectAsState()

    LazyColumn {
        items(categories) {
            ComposableCategoryItem(categoryItem = it, onPress = onCategoryPress)
        }
        item {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = colorResource(DesignSystem.color.black_12)
            )
        }
        item(deletedCategory) {
            ComposableCategoryItem(categoryItem = deletedCategory, onPress = { onDeletedPress() })
        }
    }
}
