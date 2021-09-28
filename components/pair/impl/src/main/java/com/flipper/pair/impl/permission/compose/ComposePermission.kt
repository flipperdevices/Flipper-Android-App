package com.flipper.pair.impl.permission.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipper.pair.impl.R

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
fun ComposePermission(requestPermissionButton: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.ic_scanning),
            contentDescription = stringResource(id = R.string.pair_permission_pic_hello),
        )
        Text(
            text = stringResource(id = R.string.pair_permission_title),
            style = MaterialTheme.typography.h4
        )
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(id = R.string.pair_permission_description)
        )
        TextButton(onClick = requestPermissionButton) {
            Text(text = stringResource(id = R.string.pair_permission_button))
        }
    }
}
