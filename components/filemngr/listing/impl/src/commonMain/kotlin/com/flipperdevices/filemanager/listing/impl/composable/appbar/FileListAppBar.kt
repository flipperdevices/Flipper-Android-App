package com.flipperdevices.filemanager.listing.impl.composable.appbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.listing.impl.composable.MoreIconComposable
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import com.flipperdevices.filemanager.listing.impl.viewmodel.EditFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import flipperapp.components.core.ui.res.generated.resources.ic_search
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_appbar_title
import okio.Path
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.core.ui.res.generated.resources.Res as CoreUiRes
import flipperapp.components.filemngr.listing.impl.generated.resources.Res as FML

@Suppress("LongMethod")
@Composable
fun FileListAppBar(
    selectionState: SelectionViewModel.State,
    selectionViewModel: SelectionViewModel,
    filesListState: FilesViewModel.State,
    path: Path,
    optionsState: OptionsViewModel.State,
    optionsViewModel: OptionsViewModel,
    canCreateFiles: Boolean,
    onUploadClick: () -> Unit,
    onSearchClick: () -> Unit,
    editFileViewModel: EditFileViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        modifier = modifier.background(LocalPalletV2.current.surface.navBar.body.accentBrand),
        targetState = selectionState.isEnabled,
        contentKey = { it },
        transitionSpec = {
            fadeIn()
                .plus(slideInHorizontally())
                .togetherWith(fadeOut().plus(slideOutHorizontally()))
        }
    ) { isSelectionEnabled ->
        if (isSelectionEnabled) {
            CloseSelectionAppBar(
                onDeselectAll = selectionViewModel::deselectAll,
                onClose = selectionViewModel::toggleMode,
                onSelectAll = {
                    val paths = (filesListState as? FilesViewModel.State.Loaded)
                        ?.files
                        .orEmpty()
                        .map {
                            PathWithType(
                                fileType = it.fileType ?: FileType.FILE,
                                fullPath = path.resolve(it.fileName)
                            )
                        }
                    selectionViewModel.select(paths)
                },
            )
        } else {
            OrangeAppBar(
                title = stringResource(FML.string.fml_appbar_title),
                endBlock = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .clickableRipple(onClick = onSearchClick),
                            painter = painterResource(CoreUiRes.drawable.ic_search),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        MoreIconComposable(
                            optionsState = optionsState,
                            onAction = optionsViewModel::onAction,
                            canCreateFiles = canCreateFiles,
                            onUploadClick = onUploadClick,
                            onSelectClick = selectionViewModel::toggleMode,
                            onCreateFolderClick = {
                                editFileViewModel.onCreate(path, FileType.DIR)
                            },
                            onCreateFileClick = {
                                editFileViewModel.onCreate(path, FileType.FILE)
                            }
                        )
                    }
                },
                onBack = onBack::invoke,
            )
        }
    }
}
