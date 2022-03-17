package com.flipperdevices.archive.category.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.category.R
import com.flipperdevices.archive.category.model.CategoryState
import com.flipperdevices.archive.category.viewmodels.CategoryViewModel
import com.flipperdevices.archive.category.viewmodels.CategoryViewModelFactory
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.archive.shared.composable.ComposableAppBar
import com.flipperdevices.archive.shared.composable.ComposableKeyCard
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.LocalRouter

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableCategory(
    categoryType: CategoryType = CategoryType.ByFileType(FlipperFileType.INFRARED),
    categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(categoryType)
    )
) {
    val categoryState by categoryViewModel.getState().collectAsState()
    val localCategoryState = categoryState
    val router = LocalRouter.current

    Column {
        ComposableAppBar(
            title = when (categoryType) {
                is CategoryType.ByFileType -> categoryType.fileType.humanReadableName
                CategoryType.Deleted -> stringResource(R.string.category_deleted_title)
            },
            onBack = { router.exit() }
        )
        val contentModifier = Modifier
            .weight(weight = 1f)
            .fillMaxWidth()
        when (localCategoryState) {
            is CategoryState.Loaded -> if (localCategoryState.keys.isEmpty()) {
                CategoryEmpty(contentModifier)
            } else CategoryList(contentModifier, categoryViewModel, localCategoryState.keys)
            CategoryState.Loading -> CategoryLoadingProgress(contentModifier)
        }
    }
}

@Composable
private fun CategoryList(
    modifier: Modifier,
    categoryViewModel: CategoryViewModel,
    keys: List<Pair<FlipperKeyParsed, FlipperKeyPath>>
) {
    val router = LocalRouter.current
    LazyColumn(
        modifier.padding(top = 14.dp)
    ) {
        items(keys) { (flipperKeyParsed, keyPath) ->
            ComposableKeyCard(Modifier.padding(bottom = 14.dp), flipperKeyParsed) {
                categoryViewModel.openKeyScreen(router, keyPath)
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
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.category_empty),
            color = colorResource(DesignSystem.color.black_40),
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
    }
}
