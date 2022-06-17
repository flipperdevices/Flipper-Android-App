package com.flipperdevices.core.ui.ktx

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

fun Modifier.placeholderConnecting() = composed {
    this.then(
        placeholder(
            visible = true,
            shape = RoundedCornerShape(4.dp),
            color = colorResource(id = R.color.black_8).copy(alpha = 0.2f),
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = colorResource(id = R.color.black_8)
            )
        )
    )
}
