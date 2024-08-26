package com.flipperdevices.remotecontrols.impl.brands.composable.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T> ItemsList(
    items: ImmutableList<T>,
    onClick: (T) -> Unit,
    onLongClick: (T) -> Unit,
    toCharSection: (T) -> Char,
    toString: (T) -> String,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        state = listState,
        modifier = modifier.padding(end = 14.dp)
    ) {
        itemsIndexed(items) { i, brand ->
            val charSection = remember(i) { toCharSection.invoke(brand) }
            val needDisplayTag = remember(i) {
                charSection != items.getOrNull(i - 1)?.let(toCharSection)
            }
            if (needDisplayTag) {
                Text(
                    text = "$charSection",
                    style = LocalTypography.current.bodySB14,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Column {
                Text(
                    text = toString.invoke(brand),
                    style = LocalTypography.current.bodyM14,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .combinedClickable(
                            onClick = { onClick.invoke(brand) },
                            onLongClick = { onLongClick.invoke(brand) }
                        )
                        .padding(vertical = 12.dp)
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(LocalPalletV2.current.surface.backgroundMain.separator)
                )
            }
        }
        item { Spacer(Modifier.navigationBarsPadding()) }
    }
}
