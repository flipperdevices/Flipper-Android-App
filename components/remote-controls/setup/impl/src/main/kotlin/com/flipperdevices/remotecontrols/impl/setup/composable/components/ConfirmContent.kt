package com.flipperdevices.remotecontrols.impl.setup.composable.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.remotecontrols.setup.impl.R as SetupR

@Composable
fun ConfirmContent(
    text: String,
    onPositiveClicked: () -> Unit,
    onNegativeClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(LocalPalletV2.current.surface.sheet.body.default),
        content = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(LocalPalletV2.current.surface.contentCard.separator.default)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = text,
                    style = LocalTypography.current.titleB18,
                    color = LocalPalletV2.current.text.title.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 42.dp, horizontal = 38.dp)
                ) {
                    Text(
                        text = stringResource(SetupR.string.no),
                        style = LocalTypography.current.buttonB16,
                        color = LocalPalletV2.current.action.blue.text.default,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .clickableRipple(onClick = onNegativeClicked)
                            .padding(vertical = 12.dp, horizontal = 36.dp),
                    )
                    ComposableFlipperButton(
                        text = stringResource(SetupR.string.yes),
                        onClick = onPositiveClicked
                    )
                }
            }
        }
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun ComposableConfirmContentLightPreview() {
    FlipperThemeInternal {
        ConfirmContent(
            text = "Super mega text of preview confirm element",
            onPositiveClicked = {},
            onNegativeClicked = {}
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ComposableConfirmContentDarkPreview() {
    FlipperThemeInternal {
        ConfirmContent(
            text = "Super mega text of preview confirm element",
            onPositiveClicked = {},
            onNegativeClicked = {}
        )
    }
}
