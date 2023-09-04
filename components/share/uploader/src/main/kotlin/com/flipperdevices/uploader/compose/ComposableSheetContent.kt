package com.flipperdevices.uploader.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.share.uploader.R
import com.flipperdevices.uploader.compose.content.ComposableSheetError
import com.flipperdevices.uploader.compose.content.ComposableSheetInitial
import com.flipperdevices.uploader.compose.content.ComposableSheetPending
import com.flipperdevices.uploader.compose.content.ComposableSheetPrepare
import com.flipperdevices.uploader.models.ShareContent
import com.flipperdevices.uploader.models.ShareState

@Composable
internal fun ComposableSheetContent(
    state: ShareState,
    keyName: String,
    onShareLink: (ShareContent) -> Unit,
    onShareFile: (ShareContent) -> Unit,
    onRetry: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComposableSheetFooter(keyName = keyName)
        when (state) {
            is ShareState.Error -> ComposableSheetError(state.typeError, onRetry)
            ShareState.Completed -> {
                LaunchedEffect(key1 = Unit) {
                    onClose()
                }
            }
            ShareState.Prepare -> ComposableSheetPrepare()
            ShareState.Initial -> ComposableSheetInitial()
            is ShareState.PendingShare -> ComposableSheetPending(
                onShareLink = { onShareLink(state.content) },
                onShareFile = { onShareFile(state.content) },
                isLongLink = state.content.link == null
            )
        }
    }
}

@Composable
private fun ColumnScope.ComposableSheetFooter(keyName: String) {
    Divider(
        modifier = Modifier
            .padding(top = 8.dp)
            .width(36.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = LocalPallet.current.divider12,
        thickness = 4.dp
    )
    Text(
        text = stringResource(id = R.string.share_sheet_share),
        modifier = Modifier.padding(top = 12.dp),
        style = LocalTypography.current.bodyM14
    )
    Text(
        text = keyName,
        modifier = Modifier.padding(top = 2.dp),
        style = LocalTypography.current.titleM18
    )
}
