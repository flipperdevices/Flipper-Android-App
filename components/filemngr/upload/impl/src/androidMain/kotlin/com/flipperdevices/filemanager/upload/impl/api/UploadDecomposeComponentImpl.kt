package com.flipperdevices.filemanager.upload.impl.api

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.elements.FlipperProgressIndicator
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.filemanager.upload.api.UploadDecomposeComponent
import com.flipperdevices.filemanager.upload.impl.viewmodel.UploadViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, UploadDecomposeComponent.Factory::class)
class UploadDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val path: Path,
    @Assisted private val onFinish: () -> Unit,
    private val deepLinkParser: DeepLinkParser,
    private val uploadViewModelFactory: Provider<UploadViewModel>
) : UploadDecomposeComponent(componentContext) {
    private val uploadViewModel = instanceKeeper.getOrCreate(path.toString()) {
        uploadViewModelFactory.get()
    }

    private val backCallback = BackCallback {
        uploadViewModel.onCancel()
    }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    override fun Render() {

        val context = LocalContext.current
        val pickFileLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uri ->
            val deeplinkContents = runBlocking {
                uri.map { deepLinkParser.fromUri(context, it) }
                    .filterIsInstance<Deeplink.RootLevel.SaveKey.ExternalContent>()
                    .mapNotNull { it.content }
            }
            if (deeplinkContents.isEmpty()) onBack.invoke()
            uploadViewModel.startUpload(
                path = path,
                contents = deeplinkContents
            )
        }
        LaunchedEffect(pickFileLauncher) {
            pickFileLauncher.launch("*/*")
            uploadViewModel.state
                .filter { it !is UploadViewModel.State.Uploading }
                .filter { it !is UploadViewModel.State.Pending }
                .onEach {
                    onFinish.invoke()
                }.launchIn(this)
        }
        val state by uploadViewModel.state.collectAsState()

        Scaffold(modifier = Modifier.fillMaxSize()
            .navigationBarsPadding()
            .systemBarsPadding()) { contentPadding ->
            AnimatedContent(
                targetState = state,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                contentKey = { keyState ->
                    keyState::class.simpleName
                },
            ) { animatedState ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    when (animatedState) {


                        is UploadViewModel.State.Uploading -> {
                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "Uploading...",
                                    style = LocalTypography.current.titleB18,
                                    color = LocalPalletV2.current.text.title.primary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Cancel Upload",
                                    style = LocalTypography.current.bodyM14,
                                    color = LocalPalletV2.current.action.danger.text.default,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickableRipple { uploadViewModel.onCancel() },
                                    textAlign = TextAlign.Center
                                )
                            }
                            InProgressComposable(
                                fileName = animatedState.fileName,
                                uploadedFileSize = animatedState.uploadedFileSize,
                                uploadFileTotalSize = animatedState.uploadFileTotalSize,
                                speed = 0L
                            )
                        }

                        UploadViewModel.State.Cancelled,
                        UploadViewModel.State.Error,
                        UploadViewModel.State.Pending,
                        UploadViewModel.State.Uploaded -> Unit
                    }
                }


            }
        }
    }
}

@Composable
private fun InProgressComposable(
    fileName: String,
    uploadedFileSize: Long,
    uploadFileTotalSize: Long,
    speed: Long,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (uploadFileTotalSize == 0L) 0f else uploadedFileSize / uploadFileTotalSize.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "Progress"
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = fileName,
            style = LocalTypography.current.titleB18,
            color = LocalPalletV2.current.text.title.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        FlipperProgressIndicator(
            modifier = Modifier.padding(horizontal = 32.dp),
            accentColor = LocalPalletV2.current.action.blue.border.primary.default,
            secondColor = LocalPallet.current.actionOnFlipperProgress,
            iconId = null,
            percent = animatedProgress
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Size: ${uploadedFileSize.toFormattedSize()} of ${uploadFileTotalSize.toFormattedSize()}",
            style = LocalTypography.current.subtitleM12,
            color = LocalPalletV2.current.text.body.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Speed: ${speed.toFormattedSize()}/s",
            style = LocalTypography.current.subtitleM12,
            color = LocalPalletV2.current.text.body.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun InProgressComposablePreview() {
    FlipperThemeInternal {
        InProgressComposable(
            fileName = "file_name.txt",
            uploadedFileSize = 1234L,
            uploadFileTotalSize = 1234567L,
            speed = 1222L
        )
    }
}