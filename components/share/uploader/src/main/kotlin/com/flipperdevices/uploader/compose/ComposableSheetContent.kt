package com.flipperdevices.uploader.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.share.uploader.R
import com.flipperdevices.uploader.compose.content.ComposableSheetChooser
import com.flipperdevices.uploader.compose.content.ComposableSheetError
import com.flipperdevices.uploader.models.UploaderState

@Composable
internal fun ComposableSheetContent(
    state: UploaderState,
    flipperKey: FlipperKey,
    onShareLink: () -> Unit = {},
    onShareFile: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComposableSheetFooter(keyName = flipperKey.path.nameWithExtension)
        when (state) {
            UploaderState.Chooser -> ComposableSheetChooser(
                onShareLink = onShareLink,
                onShareFile = onShareFile,
                isLongLink = flipperKey.isBig()
            )
            is UploaderState.Prepare -> {
                if (state.isLongKey) {
                    ComposableSheetPrepare(
                        titleId = R.string.share_via_secure_link_title,
                        descId = R.string.share_via_secure_link_desc
                    )
                } else {
                    ComposableSheetPrepare(
                        titleId = R.string.share_via_link_title,
                        descId = null
                    )
                }
            }
            is UploaderState.Error -> ComposableSheetError(state.typeError, onRetry)
            else -> {}
        }
    }
}

@Composable
private fun ComposableSheetFooter(keyName: String) {
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
        style = LocalTypography.current.titleB18
    )
    Text(
        text = keyName,
        modifier = Modifier.padding(top = 2.dp),
        style = LocalTypography.current.bodyR14
    )
}
