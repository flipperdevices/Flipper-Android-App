package com.flipperdevices.faphub.catalogtab.impl.composable.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.faphub.catalogtab.impl.model.CategoriesLoadState
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer

private const val DEFAULT_CATEGORIES_SIZE = 12
private const val COLUMN_COUNT = 3

@Suppress("FunctionNaming")
fun LazyListScope.ComposableCategories(
    loadState: CategoriesLoadState,
    onCategoryClick: (FapCategory) -> Unit,
    onRetry: () -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer
) {
    when (loadState) {
        is CategoriesLoadState.Loaded -> ComposableCategoriesGridItems(
            loadState.categories,
            onClick = onCategoryClick
        )

        CategoriesLoadState.Loading -> ComposableCategoriesGridItems(
            MutableList<FapCategory?>(
                DEFAULT_CATEGORIES_SIZE
            ) { null },
            onClick = onCategoryClick
        )

        is CategoriesLoadState.Error -> with(errorsRenderer) {
            ComposableThrowableErrorListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .height(height = 250.dp),
                throwable = loadState.throwable,
                onRetry = onRetry,
                fapErrorSize = FapErrorSize.IN_LIST
            )
        }
    }
}

@Suppress("FunctionNaming")
private fun LazyListScope.ComposableCategoriesGridItems(
    categories: List<FapCategory?>,
    onClick: (FapCategory) -> Unit
) = gridItems(
    data = categories,
    columnCount = COLUMN_COUNT,
    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    ComposableCategoryCard(
        modifier = if (it != null) {
            Modifier.clickableRipple { onClick(it) }
        } else {
            Modifier
        },
        fapCategory = it
    )
}
