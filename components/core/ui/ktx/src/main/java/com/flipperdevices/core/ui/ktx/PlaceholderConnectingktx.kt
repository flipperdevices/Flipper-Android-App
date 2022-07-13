package com.flipperdevices.core.ui.ktx

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

fun Modifier.placeholderConnecting(shape: Int = 4) = composed {
    this.then(
        placeholder(
            visible = true,
            shape = RoundedCornerShape(shape.dp),
            color = LocalPallet.current.placeholder.copy(alpha = 0.2f),
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = LocalPallet.current.placeholder
            )
        )
    )
}
