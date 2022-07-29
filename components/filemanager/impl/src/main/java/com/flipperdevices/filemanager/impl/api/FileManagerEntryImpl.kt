package com.flipperdevices.filemanager.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.api.navigation.FileManagerEntry
import com.flipperdevices.filemanager.impl.composable.ComposableFileManager
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.squareup.anvil.annotations.ContributesBinding
import java.net.URLEncoder
import javax.inject.Inject
import tangle.viewmodel.compose.tangleViewModel

private const val ROUTE = "filemanager/"
internal const val PATH_KEY = "path"

@ContributesBinding(AppGraph::class)
class FileManagerEntryImpl @Inject constructor() : FileManagerEntry {
    override val featureRoute = "$ROUTE{$PATH_KEY}"

    override val arguments = listOf(
        navArgument(PATH_KEY) {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun fileManagerDestination(
        path: String
    ) = "$ROUTE${URLEncoder.encode(path, "UTF-8")}"

    @Composable
    override fun NavGraphBuilder.Composable(
        navController: NavHostController,
        backStackEntry: NavBackStackEntry
    ) {
        val fileManagerViewModel: FileManagerViewModel = tangleViewModel()
        val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()
        ComposableFileManager(fileManagerState = fileManagerState) {
            navController.navigate(fileManagerDestination(it.path))
        }
    }
}
