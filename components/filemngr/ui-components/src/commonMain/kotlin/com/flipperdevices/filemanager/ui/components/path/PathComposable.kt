package com.flipperdevices.filemanager.ui.components.path

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import flipperapp.components.filemngr.ui_components.generated.resources.ic_sd_card_ok_black
import flipperapp.components.filemngr.ui_components.generated.resources.ic_sd_card_ok_white
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.painterResource
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Composable
private fun SegmentSeparatorComposable(modifier: Modifier = Modifier) {
    Text(
        text = "/",
        color = LocalPalletV2.current.text.title.tertiary,
        style = LocalTypography.current.subtitleB12,
        modifier = modifier
    )
}

@Composable
private fun MorePathComposable(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start),
    ) {
        Text(
            text = "...",
            color = LocalPalletV2.current.text.title.primary,
            style = LocalTypography.current.buttonB16,
            modifier = Modifier
        )
    }
}

@Composable
private fun SdCardIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(
            when {
                MaterialTheme.colors.isLight -> FR.drawable.ic_sd_card_ok_black
                else -> FR.drawable.ic_sd_card_ok_white
            }
        ),
        tint = Color.Unspecified,
        contentDescription = null,
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickableRipple(onClick = onClick)
    )
}

@Composable
fun PathComposable(
    path: Path,
    onRootPathClick: () -> Unit,
    onPathClick: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState(Int.MAX_VALUE)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        SdCardIcon(onClick = onRootPathClick)
        SegmentSeparatorComposable(modifier = Modifier.padding(start = 8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            MorePathComposable(
                isVisible = scrollState.canScrollBackward &&
                    !scrollState.isScrollInProgress &&
                    scrollState.viewportSize > 0
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .animateContentSize()
            ) {
                path.segments.forEachIndexed { index, segment ->
                    val isFirst = remember(index) { index == 0 }
                    if (!isFirst) {
                        SegmentSeparatorComposable()
                    }
                    Text(
                        text = segment,
                        color = LocalPalletV2.current.text.title.primary,
                        style = LocalTypography.current.buttonB16,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickableRipple {
                                val clickedPath = path.segments
                                    .subList(0, index + 1)
                                    .joinToString(
                                        separator = Path.DIRECTORY_SEPARATOR,
                                        prefix = "/"
                                    )
                                    .toPath()
                                onPathClick.invoke(clickedPath)
                            }
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
