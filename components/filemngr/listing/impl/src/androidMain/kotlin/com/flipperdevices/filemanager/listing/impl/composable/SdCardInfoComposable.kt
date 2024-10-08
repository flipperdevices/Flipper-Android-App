package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.filemanager.listing.impl.viewmodel.StorageInfoViewModel
import com.flipperdevices.filemanager.ui.components.sdcard.SdCardLoadingComposable
import com.flipperdevices.filemanager.ui.components.sdcard.SdCardMissingComposable
import com.flipperdevices.filemanager.ui.components.sdcard.SdCardOkComposable

@Composable
fun SdCardInfoComposable(
    storageInfoViewModel: StorageInfoViewModel,
    modifier: Modifier = Modifier
) {
    val state by storageInfoViewModel.state.collectAsState()
    AnimatedContent(
        targetState = state,
        modifier = modifier
            .fillMaxWidth(),
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentKey = { it::class.simpleName },
        content = { animatedState ->
            when (animatedState) {
                StorageInfoViewModel.State.Error -> {
                    SdCardMissingComposable()
                }

                is StorageInfoViewModel.State.Loaded -> {
                    SdCardOkComposable(used = animatedState.used, animatedState.free)
                }

                StorageInfoViewModel.State.Loading -> {
                    SdCardLoadingComposable()
                }
            }
        }
    )
}
