package com.flipperdevices.connection.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.connection.impl.R

private const val DONE_COLOR = 0xFF3ADEB7
private const val BLUETOOTH_COLOR = 0xFF007AFF

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableConnection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            modifier = Modifier.padding(all = 16.dp),
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = stringResource(
                id = R.string.connection_tab_search_pic_desc
            )
        )

        ConnectionStatus()

        Icon(
            modifier = Modifier.padding(all = 16.dp),
            painter = painterResource(id = R.drawable.ic_menu_dots),
            contentDescription = stringResource(
                id = R.string.connection_tab_menu_pic_desc
            )
        )
    }
}

@Composable
private fun ConnectionStatus() {
    ComposableConnectionBackground {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                tint = Color(DONE_COLOR),
                contentDescription = stringResource(
                    id = R.string.connection_status_ok_pic_desc
                )
            )

            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "Flipper Name",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_bluetooth),
                tint = Color(BLUETOOTH_COLOR),
                contentDescription = stringResource(
                    id = R.string.connection_status_bluetooth_pic_desc
                )
            )
        }
    }
}
