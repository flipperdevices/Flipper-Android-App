package com.flipperdevices.remotecontrols.impl.categories.composable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.backend.model.CategoryManifest
import com.flipperdevices.ifrmvp.backend.model.CategoryMeta
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory

@Composable
fun DeviceCategoriesLoadingContent(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(count = 8) {
            Card(
                modifier = Modifier.placeholderConnecting(),
                backgroundColor = LocalPalletV2.current.surface.contentCard.body.default,
                shape = RoundedCornerShape(12.dp),
                content = {
                    DeviceCategoryComposable(
                        onClick = {},
                        deviceCategory = DeviceCategory(
                            id = -1,
                            meta = CategoryMeta(
                                iconPngBase64 = "",
                                iconSvgBase64 = "",
                                manifest = CategoryManifest(
                                    displayName = "",
                                    singularDisplayName = ""
                                )
                            ),
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp)
                    )
                }
            )
        }
    }
}
