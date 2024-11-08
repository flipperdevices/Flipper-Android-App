package com.flipperdevices.filemanager.upload.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.deeplink.model.DeeplinkContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import okio.Path

interface UploaderDecomposeComponent {
    val state: StateFlow<State>
    val speedState: Flow<Long>

    fun onCancel()

    fun uploadRaw(
        folderPath: Path,
        fileName: String,
        content: ByteArray,
    )

    fun tryUpload(
        folderPath: Path,
        contents: List<DeeplinkContent>
    )

    @Composable
    fun Render(
        state: State,
        speedState: Long?,
        onCancelClick: () -> Unit,
        modifier: Modifier
    )

    sealed interface State {
        data object Pending : State
        data object Error : State
        data class Uploaded(val items: List<ListingItem>) : State
        data object Cancelled : State
        data class Uploading(
            val currentItemIndex: Int,
            val totalItemsAmount: Int,
            val uploadedSize: Long,
            val totalSize: Long,
            val currentItem: UploadingItem,
        ) : State
    }

    data class UploadingItem(
        val fileName: String,
        val uploadedSize: Long,
        val totalSize: Long
    )

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): UploaderDecomposeComponent
    }
}
