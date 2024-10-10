package com.flipperdevices.filemanager.listing.impl.composable.appbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.listing.impl.R
import com.flipperdevices.filemanager.listing.impl.composable.MoreIconComposable
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import com.flipperdevices.filemanager.listing.impl.viewmodel.EditFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import okio.Path

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
    editFileViewModel: EditFileViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        modifier = modifier
            .background(LocalPalletV2.current.surface.navBar.body.accentBrand),
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
                onClose = selectionViewModel::toggleMode,
                onSelectAll = {
                    val paths = (filesListState as? FilesViewModel.State.Loaded)
                        ?.files
                        .orEmpty()
                        .map {
                            val fullPath = path.resolve(it.fileName)
                            PathWithType(
                                fileType = it.fileType ?: FileType.FILE,
                                fullPath = fullPath
                            )
                        }
                    selectionViewModel.select(paths)
                },
                onDeselectAll = selectionViewModel::deselectAll
            )
        } else {
            OrangeAppBar(
                title = stringResource(R.string.fml_appbar_title),
                endBlock = {
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
                },
                onBack = onBack::invoke,
            )
        }
    }
}
