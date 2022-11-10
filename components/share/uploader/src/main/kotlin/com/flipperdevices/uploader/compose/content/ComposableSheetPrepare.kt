package com.flipperdevices.uploader.compose

import androidx.annotation.StringRes
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

@Composable
internal fun ComposableSheetPrepare(
    @StringRes titleId: Int,
    @StringRes descId: Int?
) {
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
            text = stringResource(id = titleId),
            style = LocalTypography.current.bodyM14
        )
        Spacer(modifier = Modifier.height(2.dp))
        descId?.let {
            Text(
                text = stringResource(id = it),
                style = LocalTypography.current.subtitleR10
            )
        }
    }
}
