package com.flipper.pair.impl.composable.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipper.pair.impl.R
import com.flipper.pair.impl.composable.common.ComposableAgreeButton
import com.flipper.pair.impl.composable.guide.imageslider.ComposableImageSlider
import com.flipper.pair.impl.composable.guide.imageslider.ImageSliderItem

@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
fun ComposableGuide(onNextClickListener: () -> Unit = {}) {
    Scaffold(
        bottomBar = { GuideBottomBar(onNextClickListener) }
    ) {
        GuideContent(it)
    }
}

@Composable
private fun GuideContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        Text(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            text = stringResource(R.string.pair_guide_title),
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(
                horizontal = 23.dp
            ),
            text = stringResource(R.string.pair_guide_description),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            ComposableImageSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                listOf(
                    ImageSliderItem(R.drawable.guide_1),
                    ImageSliderItem(R.drawable.guide_2),
                    ImageSliderItem(R.drawable.guide_3)
                )
            )
        }
    }
}

@Composable
private fun GuideBottomBar(
    onNextClickListener: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        ComposableAgreeButton(
            stringResource(R.string.pair_guide_ready),
            onNextClickListener
        )
    }
}
