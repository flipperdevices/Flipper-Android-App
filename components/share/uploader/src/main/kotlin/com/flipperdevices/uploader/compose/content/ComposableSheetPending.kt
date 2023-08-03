package com.flipperdevices.uploader.compose.content

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.share.uploader.R

@Composable
internal fun ComposableSheetPending(
    onShareLink: () -> Unit,
    onShareFile: () -> Unit,
    isLongLink: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 42.dp, bottom = 64.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ComposableSheetAction(
            imageId = R.drawable.ic_share_link,
            titleId = R.string.share_via_secure_link_title,
            descId = if (isLongLink) R.string.share_via_secure_link_desc else null,
            onAction = onShareLink
        )
        ComposableSheetAction(
            imageId = R.drawable.ic_share_file,
            titleId = R.string.share_export_file_title,
            descId = null,
            onAction = onShareFile
        )
    }
}

@Composable
private fun ComposableSheetAction(
    @DrawableRes imageId: Int,
    @StringRes titleId: Int,
    @StringRes descId: Int?,
    onAction: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(LocalPallet.current.shareSheetBackgroundAction.copy(alpha = 0.1f))
                .clickable(onClick = onAction),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = titleId),
            style = LocalTypography.current.bodyM14.copy(
                color = LocalPallet.current.shareSheetBackgroundAction
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = descId?.let { stringResource(id = it) } ?: "",
            style = LocalTypography.current.subtitleR10.copy(
                color = LocalPallet.current.text30
            )
        )
    }
}
