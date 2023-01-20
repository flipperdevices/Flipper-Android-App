package com.flipperdevices.faphub.installedtab.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installedtab.impl.R

@Composable
fun ComposableUpdateAllButton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LocalPallet.current.updateProgressGreen),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
            text = stringResource(R.string.faphub_installed_update_all),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.fapHubButtonText.copy(
                fontSize = 18.sp
            ),
            color = LocalPallet.current.onFapHubInstallButton
        )
        Box(
            Modifier
                .padding(end = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(LocalPallet.current.onFapHubInstallButtonCounterBackground)
        ) {
            Text(
                modifier = Modifier.padding(all = 4.dp),
                text = 4.toString(),
                textAlign = TextAlign.Center,
                style = LocalTypography.current.monoSpaceM10,
                color = LocalPallet.current.onFapHubInstallButtonCounterText
            )
        }
    }
}
