package com.flipperdevices.archive.category.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.category.R
import com.flipperdevices.archive.category.model.CategoryState
import com.flipperdevices.archive.category.viewmodels.CategoryViewModel
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.archive.shared.composable.ComposableAppBar
import com.flipperdevices.archive.shared.composable.ComposableKeyCard
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType.Companion.colorByFlipperKeyType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import kotlinx.collections.immutable.ImmutableList
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableCategory(
    categoryType: CategoryType.ByFileType,
    synchronizationUiApi: SynchronizationUiApi,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    Column(modifier = modifier) {
        ComposableAppBar(
            title = categoryType.fileType.humanReadableName,
            onBack = onBack
        )
        ComposableCategoryContent(
            categoryType = categoryType,
            synchronizationUiApi = synchronizationUiApi,
            onOpenKeyScreen = onOpenKeyScreen
        )
    }
}

@Composable
fun ColumnScope.ComposableCategoryContent(
    categoryType: CategoryType,
    synchronizationUiApi: SynchronizationUiApi?,
    categoryViewModel: CategoryViewModel = tangleViewModel(),
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    val categoryState by categoryViewModel.getState().collectAsState()
    val synchronizationState by categoryViewModel.getSynchronizationState().collectAsState()
    val localCategoryState = categoryState

    val contentModifier = Modifier
        .weight(weight = 1f)
        .fillMaxWidth()
    when (localCategoryState) {
        is CategoryState.Loaded -> if (localCategoryState.keys.isEmpty()) {
            CategoryEmpty(contentModifier)
        } else {
            CategoryList(
                categoryType,
                synchronizationUiApi,
                synchronizationState,
                localCategoryState.keys,
                contentModifier,
                onOpenKeyScreen
            )
        }
        CategoryState.Loading -> CategoryLoadingProgress(contentModifier)
    }
}

@Composable
private fun CategoryList(
    categoryType: CategoryType,
    synchronizationUiApi: SynchronizationUiApi?,
    synchronizationState: SynchronizationState,
    keys: ImmutableList<Pair<FlipperKeyParsed, FlipperKey>>,
    modifier: Modifier = Modifier,
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    LazyColumn(
        modifier.padding(top = 14.dp)
    ) {
        items(keys) { (flipperKeyParsed, flipperKey) ->
            ComposableKeyCard(
                modifier = Modifier.padding(bottom = 14.dp),
                synchronizationContent = if (synchronizationUiApi != null) {
                    { ->
                        synchronizationUiApi.RenderSynchronizationState(
                            synced = flipperKey.synchronized,
                            synchronizationState = synchronizationState,
                            withText = false
                        )
                    }
                } else {
                    null
                },
                flipperKeyParsed = flipperKeyParsed,
                typeColor = when (categoryType) {
                    is CategoryType.ByFileType -> colorByFlipperKeyType(categoryType.fileType)
                    CategoryType.Deleted -> LocalPallet.current.keyDeleted
                },
                onCardClicked = {
                    onOpenKeyScreen(flipperKey.getKeyPath())
                }
            )
        }
    }
}

@Composable
private fun CategoryLoadingProgress(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CategoryEmpty(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.category_empty),
            color = LocalPallet.current.text40,
            style = LocalTypography.current.bodyR16
        )
    }
}
