package com.flipperdevices.connection.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.composable.status.ComposableConnectionStatus

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableConnection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(all = 16.dp),
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = stringResource(
                id = R.string.connection_tab_search_pic_desc
            )
        )

        Box(
            Modifier.weight(weight = 1f),
            contentAlignment = Alignment.Center
        ) {
            ComposableConnectionStatus()
        }

        Icon(
            modifier = Modifier.padding(all = 16.dp),
            painter = painterResource(id = R.drawable.ic_menu_dots),
            contentDescription = stringResource(
                id = R.string.connection_tab_menu_pic_desc
            )
        )
    }
}
