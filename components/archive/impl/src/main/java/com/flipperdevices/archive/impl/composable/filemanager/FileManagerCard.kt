package com.flipperdevices.archive.impl.composable.filemanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun FileManagerCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickableRipple(onClick = onClick)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            if (MaterialTheme.colors.isLight) {
                                R.drawable.ic_file_dark
                            } else {
                                R.drawable.ic_file_light
                            }
                        ),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(R.string.archive_card_file_manager_title),
                        style = LocalTypography.current.buttonB16,
                        color = LocalPalletV2.current.text.title.primary
                    )
                }

                Icon(
                    painter = painterResource(DesignSystem.drawable.ic_forward),
                    contentDescription = null,
                    tint = LocalPallet.current.iconTint30
                )
            }
            Text(
                text = stringResource(R.string.archive_card_file_manager_desc),
                style = LocalTypography.current.bodyR14,
                color = LocalPalletV2.current.text.caption.secondary
            )
        }
    }
}

@Preview
@Composable
private fun FileManagerCardPreview() {
    FlipperThemeInternal {
        FileManagerCard(onClick = {})
    }
}
