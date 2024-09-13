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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.category.R
import com.flipperdevices.archive.category.model.CategoryState
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.archive.shared.composable.ComposableKeyCard
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType.Companion.colorByFlipperKeyType
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ComposableCategory(
    categoryType: CategoryType.ByFileType,
    categoryState: CategoryState,
    synchronizationState: SynchronizationState,
    synchronizationUiApi: SynchronizationUiApi,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    Column(modifier = modifier) {
        OrangeAppBar(
            title = categoryType.fileType.humanReadableName,
            onBack = onBack,
        )
        ComposableCategoryContent(
            categoryType = categoryType,
            synchronizationUiApi = synchronizationUiApi,
            onOpenKeyScreen = onOpenKeyScreen,
            categoryState = categoryState,
            synchronizationState = synchronizationState
        )
    }
}

@Composable
fun ColumnScope.ComposableCategoryContent(
    categoryType: CategoryType,
    synchronizationUiApi: SynchronizationUiApi?,
    categoryState: CategoryState,
    synchronizationState: SynchronizationState,
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    val contentModifier = Modifier
        .weight(weight = 1f)
        .fillMaxWidth()
    when (categoryState) {
        is CategoryState.Loaded -> if (categoryState.keys.isEmpty()) {
            CategoryEmpty(contentModifier)
        } else {
            CategoryList(
                categoryType,
                synchronizationUiApi,
                synchronizationState,
                categoryState.keys,
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
                onCardClick = {
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
