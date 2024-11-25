package com.flipperdevices.filemanager.upload.impl.api

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.filemanager.upload.api.MultipleFilesPicker
import com.flipperdevices.filemanager.upload.api.UploadDecomposeComponent
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path

@ContributesAssistedFactory(AppGraph::class, UploadDecomposeComponent.Factory::class)
class UploadDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onFilesChanged: (List<ListingItem>) -> Unit,
    private val deepLinkParser: DeepLinkParser,
    uploaderDecomposeComponentFactory: UploaderDecomposeComponent.Factory
) : ComponentContext by componentContext,
    UploadDecomposeComponent {

    private val uploaderDecomposeComponent = uploaderDecomposeComponentFactory.invoke(
        componentContext = childContext("upload_dc_uploaderDecomposeComponent")
    )

    private val backCallback = BackCallback {
        uploaderDecomposeComponent.onCancel()
    }

    @Composable
    override fun rememberMultipleFilesPicker(path: Path): MultipleFilesPicker {
        val context = LocalContext.current
        val pickFileLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uri ->
            val deeplinkContents = runBlocking {
                uri.map { deepLinkParser.fromUri(context, it) }
                    .filterIsInstance<Deeplink.RootLevel.SaveKey.ExternalContent>()
                    .mapNotNull { it.content }
            }
            if (deeplinkContents.isEmpty()) return@rememberLauncherForActivityResult
            uploaderDecomposeComponent.tryUpload(
                folderPath = path,
                contents = deeplinkContents
            )
        }
        return MultipleFilesPicker { pickFileLauncher.launch("*/*") }
    }

    @Composable
    override fun Render() {
        val state by uploaderDecomposeComponent.state.collectAsState()
        val speedState by uploaderDecomposeComponent.speedState.collectAsState(null)

        LaunchedEffect(uploaderDecomposeComponent) {
            uploaderDecomposeComponent.state
                .onEach {
                    when (it) {
                        is UploaderDecomposeComponent.State.Uploaded,
                        is UploaderDecomposeComponent.State.Pending,
                        UploaderDecomposeComponent.State.Error,
                        UploaderDecomposeComponent.State.Cancelled -> {
                            if (backHandler.isRegistered(backCallback)) {
                                backHandler.unregister(backCallback)
                            }
                        }

                        is UploaderDecomposeComponent.State.Uploading -> {
                            if (!backHandler.isRegistered(backCallback)) {
                                backHandler.register(backCallback)
                            }
                            val item = ListingItem(
                                fileName = it.currentItem.fileName,
                                fileType = FileType.FILE,
                                size = it.currentItem.uploadedSize
                            )
                            onFilesChanged.invoke(listOf(item))
                        }
                    }
                }.launchIn(this)
        }
        uploaderDecomposeComponent.Render(
            state = state,
            speedState = speedState,
            onCancelClick = uploaderDecomposeComponent::onCancel,
            modifier = Modifier
                .fillMaxSize()
                .background(LocalPalletV2.current.surface.backgroundMain.body)
                .navigationBarsPadding()
                .systemBarsPadding(),
        )
    }
}
