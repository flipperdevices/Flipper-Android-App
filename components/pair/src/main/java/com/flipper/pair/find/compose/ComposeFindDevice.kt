package com.flipper.pair.find.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipper.pair.R

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SimplePreview() {
    Column(
        Modifier.width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.ic_scanning),
            contentDescription = stringResource(id = R.string.finddevice_pic_search_devices),
        )
        Text(text = stringResource(id = R.string.finddevice_title))

    }
}