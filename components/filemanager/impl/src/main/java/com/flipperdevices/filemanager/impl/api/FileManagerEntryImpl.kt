package com.flipperdevices.filemanager.impl.api

import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.api.navigation.FileManagerEntry
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerScreen
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerUploadedScreen
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.filemanager.impl.viewmodels.ReceiveViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel

private const val ROUTE = "@filemanager"
internal const val PATH_KEY = "path"
internal const val CONTENT_KEY = "content"
private const val FILE_MANAGER_ROUTE = "filemanager?path={$PATH_KEY}"
private const val FILE_MANAGER_UPLOAD_ROUTE =
    "filemanagerupload?path={$PATH_KEY}&content={$CONTENT_KEY}"

@ContributesBinding(AppGraph::class)
class FileManagerEntryImpl @Inject constructor(
    private val deepLinkParser: DeepLinkParser
) : FileManagerEntry {
    private val fileManagerArguments = listOf(
        navArgument(PATH_KEY) {
            type = NavType.StringType
            nullable = false
        }
    )
    private val uploadArguments = fileManagerArguments.plus(
        navArgument(CONTENT_KEY) {
            type = DeeplinkContentType()
            nullable = false
        }
    )

    override fun fileManagerDestination(
        path: String
    ) = "filemanager?path=${Uri.encode(path)}"

    override fun uploadFile(
        path: String,
        deeplinkContent: DeeplinkContent
    ) = "filemanagerupload?path=${Uri.encode(path)}" +
        "&content=${Uri.encode(Json.encodeToString(deeplinkContent))}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = fileManagerDestination(), route = ROUTE) {
            composable(FILE_MANAGER_ROUTE, fileManagerArguments) {
                val fileManagerViewModel: FileManagerViewModel = tangleViewModel()
                val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()
                ComposableFileManagerScreen(
                    fileManagerState = fileManagerState,
                    onOpenFolder = { navController.navigate(fileManagerDestination(it.path)) },
                    deepLinkParser = deepLinkParser,
                    onUploadFile = {
                        navController.navigate(
                            uploadFile(
                                fileManagerState.currentPath,
                                it
                            )
                        )
                    }
                )
            }
            composable(FILE_MANAGER_UPLOAD_ROUTE, uploadArguments) {
                val fileManagerViewModel: FileManagerViewModel = tangleViewModel()
                val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()

                val receiveViewModel: ReceiveViewModel = tangleViewModel()
                val shareState by receiveViewModel.getReceiveState().collectAsState()

                if (shareState.processCompleted) {
                    navController.popBackStack()
                }

                ComposableFileManagerUploadedScreen(
                    fileManagerState,
                    shareState
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}
