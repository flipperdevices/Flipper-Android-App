package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.composable.key.ComposableKeySmall
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.R as DesignSystem

@Composable
internal fun ColumnScope.AllKeysList(
    keys: List<FlipperKey>,
    onKeyClick: (FlipperKey) -> Unit
) {
    Text(
        modifier = Modifier.padding(top = 24.dp, start = 14.dp),
        text = stringResource(R.string.archive_tab_general_all_title),
        fontWeight = FontWeight.W700,
        fontSize = 16.sp,
        color = colorResource(DesignSystem.color.black_100)
    )

    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 7.dp),
        cells = GridCells.Fixed(count = 2),
    ) {
        items(keys) {
            ComposableKeySmall(it.path) {
                onKeyClick(it)
            }
        }
    }
}
