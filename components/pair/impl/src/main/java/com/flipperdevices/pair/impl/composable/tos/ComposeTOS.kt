package com.flipperdevices.pair.impl.composable.tos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.pair.impl.R
import com.flipperdevices.pair.impl.composable.common.ComposableAgreeButton

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableTOS(
    onAgreeClickListener: () -> Unit = {}
) {
    Scaffold(bottomBar = {
        TOSBottomBar(onAgreeClickListener)
    }) {
        Box(Modifier.padding(it)) {
            TOSContent()
        }
    }
}

@Composable
fun TOSContent() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.pair_tos_welcome),
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center
        )
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(R.drawable.ic_welcome),
            contentDescription = stringResource(R.string.pair_tos_pic_welcome)
        )
        Text(
            modifier = Modifier.padding(all = 16.dp),
            text = stringResource(R.string.pair_tos_title),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.pair_tos_description),
            fontSize = 14.sp
        )
    }
}

@Composable
fun TOSBottomBar(onAgreeClickListener: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ComposableAgreeButton(stringResource(R.string.pair_tos_button_text), onAgreeClickListener)
    }
}
