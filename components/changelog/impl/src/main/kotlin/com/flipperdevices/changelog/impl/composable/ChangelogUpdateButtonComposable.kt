package com.flipperdevices.changelog.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.flipperdevices.changelog.impl.R
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ChangelogUpdateButtonComposable(
    isUpdate: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val buttonColor = if (isUpdate) {
        LocalPalletV2.current.action.fwUpdate.background.primary.default
    } else {
        LocalPalletV2.current.action.fwInstall.background.primary.default
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(buttonColor)
            .clickableRipple(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = if (isUpdate) {
                stringResource(R.string.whatsnew_btn_update)
            } else {
                stringResource(R.string.whatsnew_btn_install)
            },
            textAlign = TextAlign.Center,
            style = LocalTypography.current.updateButton40,
            color = if (isUpdate) {
                LocalPalletV2.current.action.fwUpdate.text.onColor
            } else {
                LocalPalletV2.current.action.fwInstall.text.onColor
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ChangelogUpdateButtonComposablePreview() {
    FlipperThemeInternal {
        ChangelogUpdateButtonComposable(
            isUpdate = true,
            onClick = {}
        )
    }
}
