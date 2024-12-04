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
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_copy_to
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_dialog_delete_btn
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_export
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_file
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_folder
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_move_to
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_select
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_rename
import flipperapp.components.filemngr.ui_components.generated.resources.ic_copy_to
import flipperapp.components.filemngr.ui_components.generated.resources.ic_edit
import flipperapp.components.filemngr.ui_components.generated.resources.ic_move
import flipperapp.components.filemngr.ui_components.generated.resources.ic_select
import flipperapp.components.filemngr.ui_components.generated.resources.ic_trash_white
import flipperapp.components.filemngr.ui_components.generated.resources.ic_upload
import okio.Path
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.listing.impl.generated.resources.Res as FML
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

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
                painter = painterResource(FR.drawable.ic_copy_to),
                onClick = onCopyTo,
                isEnabled = false
            )
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_move_to),
                painter = painterResource(FR.drawable.ic_move),
                onClick = onMoveTo,
                isEnabled = true
            )
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_export),
                painter = painterResource(FR.drawable.ic_upload),
                onClick = onExport,
                isEnabled = fileType == FileType.FILE
            )
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_rename),
                painter = painterResource(FR.drawable.ic_edit),
                onClick = onRename,
                isEnabled = true
            )
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_otp_select),
                painter = painterResource(FR.drawable.ic_select),
                onClick = onSelect
            )
            Divider()
            HorizontalTextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(FML.string.fml_dialog_delete_btn),
                painter = painterResource(FR.drawable.ic_trash_white),
                iconTint = LocalPalletV2.current.action.danger.icon.default,
                textColor = LocalPalletV2.current.action.danger.text.default,
                onClick = onDelete
            )
        }
    }
}
