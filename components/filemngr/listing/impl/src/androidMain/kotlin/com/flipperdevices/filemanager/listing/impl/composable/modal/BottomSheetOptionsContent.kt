package com.flipperdevices.filemanager.listing.impl.composable.modal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.ui.components.R
import okio.Path
import okio.Path.Companion.toPath
import com.flipperdevices.filemanager.listing.impl.R as FML

@Composable
private fun Title(fileType: FileType, path: Path) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (fileType) {
                FileType.FILE -> stringResource(FML.string.fml_file)
                FileType.DIR -> stringResource(FML.string.fml_folder)
            },
            style = LocalTypography.current.bodyR14,
            color = LocalPalletV2.current.text.label.primary
        )
        Text(
            text = path.name,
            style = LocalTypography.current.titleB18,
            color = LocalPalletV2.current.text.label.primary
        )
    }
}

@Composable
fun BottomSheetOptionsContent(
    fileType: FileType,
    path: Path,
    onCopyTo: () -> Unit,
    onMoveTo: () -> Unit,
    onExport: () -> Unit,
    onRename: () -> Unit,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Title(
            fileType = fileType,
            path = path
        )
        Spacer(Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_copy_to),
                painter = painterResource(R.drawable.ic_copy_to),
                onClick = onCopyTo
            )
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_move_to),
                painter = painterResource(R.drawable.ic_move),
                onClick = onMoveTo
            )
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_export),
                painter = painterResource(R.drawable.ic_upload),
                onClick = onExport
            )
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_rename),
                painter = painterResource(R.drawable.ic_edit),
                onClick = onRename
            )
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_otp_select),
                painter = painterResource(R.drawable.ic_select),
                onClick = onSelect
            )
            Divider()
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_dialog_delete_btn),
                painter = painterResource(R.drawable.ic_trash_white),
                iconTint = LocalPalletV2.current.action.danger.icon.default,
                textColor = LocalPalletV2.current.action.danger.text.default,
                onClick = onDelete
            )
        }
    }
}

@Preview
@Composable
private fun BottomSheetOptionsPreview() {
    FlipperThemeInternal {
        BottomSheetOptionsContent(
            fileType = FileType.DIR,
            path = "some_file.ir".toPath(),
            onExport = {},
            onDelete = {},
            onCopyTo = {},
            onRename = {},
            onSelect = {},
            onMoveTo = {}
        )
    }
}
