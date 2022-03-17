package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.search.R

@Composable
fun ComposableSearchBar() {
}

@Composable
fun ComposableSearchBarBack(onBack: () -> Unit) {
    Icon(
        modifier = Modifier
            .size(size = 24.dp)
            .padding(start = 24.dp, top = 14.dp, bottom = 14.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = onBack
            ),
        painter = painterResource(R.drawable.ic_back_arrow),
        contentDescription = null
    )
}
