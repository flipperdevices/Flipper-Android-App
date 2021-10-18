package com.flipperdevices.pair.impl.composable.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.pair.impl.R

val TITLE_TOP_MARGIN = 32.dp

@Composable
fun ComposePairScreen(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.padding(top = TITLE_TOP_MARGIN),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h3
        )
        if (description != null) {
            Text(
                modifier = Modifier.padding(all = 16.dp),
                text = description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle1
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun PreviewComposePairScreen() {
    ComposePairScreen(
        title = "Test Title",
        description = "Test description"
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_find),
            contentDescription = "Test"
        )
    }
}
