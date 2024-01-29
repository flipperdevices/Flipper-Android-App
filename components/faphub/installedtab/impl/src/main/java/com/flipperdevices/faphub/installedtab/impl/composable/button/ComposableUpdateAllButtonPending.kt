package com.flipperdevices.faphub.installedtab.impl.composable.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installedtab.impl.R

@Composable
fun ComposableUpdateAllButtonPending(
    pendingCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(LocalPallet.current.updateProgressGreen),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 9.dp, bottom = 9.dp, end = 12.dp),
            text = stringResource(R.string.faphub_installed_update_all),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.fapHubButtonText.copy(
                fontSize = 18.sp
            ),
            color = LocalPallet.current.onFapHubInstallButton
        )

        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(LocalPallet.current.onFapHubInstallButton.copy(alpha = 0.8f))
        ) {
            Text(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 7.dp),
                text = pendingCount.toString(),
                style = LocalTypography.current.titleB18,
                textAlign = TextAlign.Center,
                color = LocalPallet.current.updateProgressGreen
            )
        }
    }
}
