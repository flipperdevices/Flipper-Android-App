package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.composable.key.ComposableKeySmall
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.R as DesignSystem

private const val GRID_WIDTH = 2
private const val GRID_ROW_WEIGHT = 1f / GRID_WIDTH

@Suppress("FunctionName")
fun LazyListScope.ComposableKeysGrid(keys: List<FlipperKey>) {
    items(keys.windowed(GRID_WIDTH, GRID_WIDTH, partialWindows = true)) { items ->
        Row(
            modifier = Modifier
                .padding(horizontal = 7.dp)
                .fillMaxWidth()
        ) {
            items.forEach {
                ComposableKeySmall(
                    modifier = Modifier.weight(GRID_ROW_WEIGHT),
                    keyPath = it.path
                )
            }
            repeat(GRID_WIDTH - items.size) {
                Box(Modifier.weight(GRID_ROW_WEIGHT))
            }
        }
    }
}

@Composable
fun ComposableFavoriteKeysTitle() {
    Row(modifier = Modifier.padding(top = 24.dp, start = 14.dp)) {
        Text(
            text = stringResource(R.string.archive_tab_general_all_title),
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            color = colorResource(com.flipperdevices.core.ui.R.color.black_100)
        )
        Icon(
            modifier = Modifier.size(size = 16.dp),
            painter = painterResource(DesignSystem.drawable.ic_star_enabled),
            tint = colorResource(DesignSystem.color.favorite_enabled),
            contentDescription = null
        )
    }
}

@Composable
fun ComposableAllKeysTitle() {
    Text(
        modifier = Modifier.padding(top = 24.dp, start = 14.dp),
        text = stringResource(R.string.archive_tab_general_all_title),
        fontWeight = FontWeight.W700,
        fontSize = 16.sp,
        color = colorResource(DesignSystem.color.black_100)
    )
}
