package com.flipperdevices.remotecontrols.impl.categories.composable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent

@Composable
internal fun DeviceCategoriesLoadedContent(
    model: DeviceCategoriesComponent.Model.Loaded,
    onCategoryClick: (DeviceCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(model.deviceTypes) { deviceCategory ->
            DeviceCategoryComposable(
                deviceCategory = deviceCategory,
                onClick = {
                    onCategoryClick.invoke(deviceCategory)
                }
            )
        }
    }
}
