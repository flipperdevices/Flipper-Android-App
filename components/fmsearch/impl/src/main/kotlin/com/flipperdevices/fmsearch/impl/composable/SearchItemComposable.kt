package com.flipperdevices.fmsearch.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.fmsearch.impl.R
import com.flipperdevices.fmsearch.impl.model.SearchItem

@Composable
fun SearchItemComposable(
    searchItem: SearchItem,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    Icon(
        modifier = Modifier
            .padding(all = 8.dp)
            .size(48.dp),
        painter = painterResourceByKey(
            id = if (searchItem.isFolder) {
                R.drawable.ic_folder
            } else {
                R.drawable.ic_file
            }
        ),
        contentDescription = null
    )
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            style = MaterialTheme.typography.h5,
            text = searchItem.name
        )

        Text(
            style = MaterialTheme.typography.h5,
            text = searchItem.path
        )
    }
}
