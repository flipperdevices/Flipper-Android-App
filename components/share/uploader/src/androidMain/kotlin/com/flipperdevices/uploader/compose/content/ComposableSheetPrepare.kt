package com.flipperdevices.uploader.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.share.uploader.R

@Composable
internal fun ComposableSheetPrepare() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 54.dp, bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = LocalPallet.current.accentSecond,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.share_via_secure_link_title),
            style = LocalTypography.current.bodyM14.copy(
                color = LocalPallet.current.shareSheetBackgroundAction
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(id = R.string.share_via_secure_link_desc),
            style = LocalTypography.current.subtitleR10.copy(
                color = LocalPallet.current.text30
            )
        )
    }
}
