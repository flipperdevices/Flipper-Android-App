package com.flipperdevices.faphub.maincard.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.maincard.impl.R
import com.flipperdevices.faphub.maincard.impl.composable.suggestion.ComposableSuggestion
import com.flipperdevices.faphub.maincard.impl.model.FapMainCardState
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableMainCardInternal(
    state: FapMainCardState,
    isExistAppReadyToUpdate: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickableRipple(onClick = onClick)
        ) {
            ComposableTitle(isExistAppReadyToUpdate)
            ComposableSuggestion(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
                state = state
            )
        }
    }
}

@Composable
private fun ComposableTitle(
    isExistAppReadyToUpdate: Boolean
) {
    val title = stringResource(R.string.maincard_title)
    Row(
        modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(R.drawable.pic_application),
            contentDescription = title,
            tint = LocalPallet.current.text100
        )
        Text(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp)
                .weight(1f),
            text = title,
            style = LocalTypography.current.titleSB16,
            color = LocalPallet.current.text100
        )

        if (isExistAppReadyToUpdate) {
            ComposableUpdatesAvailableBubble()
        }

        Icon(
            modifier = Modifier
                .size(14.dp),
            painter = painterResource(DesignSystem.drawable.ic_navigate),
            contentDescription = title,
            tint = LocalPallet.current.text16
        )
    }
}

@Composable
private fun ComposableUpdatesAvailableBubble() {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LocalPallet.current.updateProgressGreen)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = stringResource(R.string.maincard_update_available),
            color = LocalPallet.current.onFlipperButton,
            style = LocalTypography.current.subtitleB10
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun ComposableTitlePreview() {
    FlipperThemeInternal {
        ComposableTitle(isExistAppReadyToUpdate = true)
    }
}
