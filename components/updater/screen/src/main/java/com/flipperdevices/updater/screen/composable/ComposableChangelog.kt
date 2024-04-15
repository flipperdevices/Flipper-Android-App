package com.flipperdevices.updater.screen.composable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.ComposableMarkdown
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.screen.R
import com.flipperdevices.updater.screen.helper.ChangelogFormatter

@Composable
fun ComposableChangelog(
    changelog: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var heightColumn by remember { mutableIntStateOf(0) }
    Column(modifier.fillMaxSize()) {
        Divider(
            Modifier
                .height(1.dp)
                .background(LocalPallet.current.divider12)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .onGloballyPositioned { heightColumn = it.size.height } // Height column
                .verticalScrollbar(
                    color = LocalPallet.current.divider12,
                    state = scrollState,
                    heightBar = 46.dp,
                    widthBar = 4.dp,
                    heightColumn = heightColumn,
                    paddingTop = 24.dp,
                    paddingBottom = 12.dp
                )
                .padding(top = 24.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = R.string.update_screen_whats_new),
                style = LocalTypography.current.titleSB18,
                textAlign = TextAlign.Left
            )
            ComposableMarkdown(
                content = ChangelogFormatter.format(changelog),
                modifier = Modifier.fillMaxSize().padding(4.dp)
            )
        }
        Divider(
            Modifier
                .height(1.dp)
                .background(LocalPallet.current.divider12)
        )
    }
}

@Suppress("LongParameterList")
fun Modifier.verticalScrollbar(
    color: Color,
    state: ScrollState,
    heightBar: Dp,
    widthBar: Dp,
    heightColumn: Int,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp
) = drawWithContent {
    // Calculate what part of the scroll is passed
    val partByScrollBar = state.value / (state.maxValue.toFloat())

    // padding top/bottom and height bar
    val heightDifference = (heightBar + paddingTop + paddingBottom).toPx()
    val scrollBoxHeight = (heightColumn - heightDifference)
    // Calculate coordinate y for scrollbar in column box(not scroll box)
    val offsetYScrollBar = paddingTop.toPx() + partByScrollBar * scrollBoxHeight

    // Calculate coordinate x for scrollbar
    val offsetXScrollBar = this.size.width - widthBar.toPx()

    drawContent()
    drawRoundRect(
        color = color,
        topLeft = Offset(offsetXScrollBar, offsetYScrollBar),
        size = Size(widthBar.toPx(), heightBar.toPx()),
        cornerRadius = CornerRadius(x = 100f, y = 100f)
    )
}
