package com.flipperdevices.filemanager.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.api.navigation.FileManagerEntry
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerDownloadScreen
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerEditorScreen
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerScreen
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerUploadedScreen
import com.flipperdevices.filemanager.impl.model.ShareFile
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import java.io.File
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val ROUTE = "@filemanager"
internal const val PATH_KEY = "path"
internal const val CONTENT_KEY = "content"
internal const val FILE_PATH_KEY = "file_path"
private const val FILE_MANAGER_ROUTE = "filemanager?path={$PATH_KEY}"
private const val FILE_MANAGER_UPLOAD_ROUTE =
    "filemanagerupload?path={$PATH_KEY}&content={$CONTENT_KEY}"
private const val FILE_MANAGER_DOWNLOAD_ROUTE =
    "filemanagerdownload?path={$PATH_KEY}&filepath={$FILE_PATH_KEY}"
private const val FILE_MANAGER_EDITOR_ROUTE =
    "filemanagereditor?filepath={$FILE_PATH_KEY}"

@ContributesBinding(AppGraph::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
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
    private val downloadArguments = fileManagerArguments.plus(
        navArgument(FILE_PATH_KEY) {
            type = ShareFileType()
            nullable = false
        }
    )
    private val editorArguments = listOf(
        navArgument(FILE_PATH_KEY) {
            type = ShareFileType()
            nullable = false
        }
    )

    override fun fileManagerDestination(
        path: String
    ) = "filemanager?path=${Uri.encode(path)}"

    private fun uploadFileDestination(
        path: String,
        deeplinkContent: DeeplinkContent
    ) = "filemanagerupload?path=${Uri.encode(path)}" +
            "&content=${Uri.encode(Json.encodeToString(deeplinkContent))}"

    private fun downloadFileDestination(
        file: ShareFile,
        pathToDirectory: String = File(file.flipperFilePath).absoluteFile.parent ?: "/"
    ) = "filemanagerdownload?path=${Uri.encode(pathToDirectory)}" +
            "&filepath=${Uri.encode(Json.encodeToString(file))}"

    private fun editorFileDestination(
        file: ShareFile
    ) = "filemanagereditor?filepath=${Uri.encode(Json.encodeToString(file))}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = fileManagerDestination(), route = ROUTE) {
            composable(FILE_MANAGER_ROUTE, fileManagerArguments) {
                ComposableFileManagerScreen(
                    deepLinkParser,
                    onOpenFolder = {
                        navController.navigate(fileManagerDestination(it.path))
                    },
                    onDownloadAndShareFile = {
                        navController.navigate(downloadFileDestination(ShareFile(it)))
                    },
                    onOpenEditor = {
                        navController.navigate(editorFileDestination(ShareFile(it)))
                    },
                    onUploadFile = { path, content ->
                        navController.navigate(
                            uploadFileDestination(
                                path,
                                content
                            )
                        )
                    }
                )
            }
            composable(FILE_MANAGER_UPLOAD_ROUTE, uploadArguments) {
                ComposableFileManagerUploadedScreen(navController)
            }
            composable(FILE_MANAGER_DOWNLOAD_ROUTE, downloadArguments) {
                ComposableFileManagerDownloadScreen(navController)
            }
            composable(FILE_MANAGER_EDITOR_ROUTE, editorArguments) {
                ComposableFileManagerEditorScreen(navController)
            }
        }
    }
}
