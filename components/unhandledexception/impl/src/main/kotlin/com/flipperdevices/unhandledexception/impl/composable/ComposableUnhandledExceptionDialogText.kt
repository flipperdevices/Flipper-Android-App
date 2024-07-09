package com.flipperdevices.unhandledexception.impl.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.unhandledexception.impl.R
import java.lang.Integer.max

private data class Line(val isOrdered: Boolean, @StringRes val textId: Int)

@Composable
@Suppress("LongMethod")
fun ComposableUnhandledExceptionDialogText(modifier: Modifier = Modifier) {
    val lines = remember {
        listOf(
            Line(isOrdered = true, R.string.unhandledexception_dialog_desc_1),
            Line(isOrdered = true, R.string.unhandledexception_dialog_desc_2),
            Line(isOrdered = false, R.string.unhandledexception_dialog_desc_2_1),
            Line(isOrdered = false, R.string.unhandledexception_dialog_desc_2_2),
            Line(isOrdered = true, R.string.unhandledexception_dialog_desc_3)
        )
    }

    var columnWidth by remember { mutableIntStateOf(0) }
    val textStyle = LocalTypography.current.bodyR14.copy(
        color = LocalPallet.current.text40,
        lineHeight = 19.6.sp,
    )

    Column(modifier) {
        Text(
            text = stringResource(R.string.unhandledexception_dialog_desc_0),
            style = textStyle
        )
        var currentLineIndex = 1
        lines.forEach { line ->
            Row(Modifier.padding(horizontal = 6.dp)) {
                Text(
                    modifier = Modifier.layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)

                        val existingWidth = columnWidth
                        val maxWidth = maxOf(existingWidth, placeable.width)

                        if (maxWidth > existingWidth) {
                            columnWidth = maxWidth
                        }

                        layout(width = maxWidth, height = placeable.height) {
                            val x = if (line.isOrdered) {
                                0
                            } else {
                                max(0, maxWidth / 2 - placeable.width)
                            }
                            placeable.placeRelative(x, 0)
                        }
                    },
                    text = if (line.isOrdered) {
                        "${currentLineIndex++}. "
                    } else {
                        "•"
                    },
                    textAlign = if (line.isOrdered) {
                        TextAlign.Start
                    } else {
                        TextAlign.Center
                    },
                    style = textStyle
                )
                Text(
                    text = stringResource(line.textId),
                    style = textStyle,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableUnhandledExceptionDialogTextPreview() {
    FlipperThemeInternal {
        Box {
            ComposableUnhandledExceptionDialogText()
        }
    }
}
