package com.flipperdevices.remotecontrols.impl.setup.composable.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.remotecontrols.setup.impl.R

@Composable
fun PointFlipperComposable(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(12.dp),
                color = LocalPallet.current.text12
            )
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(R.string.rcs_image_tutorial_title),
            style = LocalTypography.current.titleSB16,
            color = LocalPallet.current.text60
        )
        Image(
            painter = painterResource(
                id = when (MaterialTheme.colors.isLight) {
                    true -> R.drawable.img_setup_remote_light
                    false -> R.drawable.img_setup_remote_dark
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp)
        )

        Text(
            text = stringResource(R.string.rcs_image_tutorial_desc),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text30
        )
    }
}

@Preview
@Composable
private fun PointFlipperComposablePreview() {
    FlipperThemeInternal {
        PointFlipperComposable()
    }
}
