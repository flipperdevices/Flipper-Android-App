package com.flipperdevices.filemanager.impl.fragment

import androidx.fragment.app.activityViewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.core.view.ComposeFragment
import com.flipperdevices.filemanager.impl.composable.ComposableFileManager
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.Storage
import com.flipperdevices.protobuf.storage.listRequest
import com.flipperdevices.service.FlipperViewModel
import com.flipperdevices.service.FlipperViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce

class FileManagerFragment : ComposeFragment() {
    private val bleViewModel by activityViewModels<FlipperViewModel> {
        FlipperViewModelFactory(requireActivity().application, getDeviceId())
    }

    @ExperimentalCoroutinesApi
    @Composable
    override fun renderView() {
        val fileList by getFilesForDir(getDirectory())
            .collectAsState(emptyList())

        ComposableFileManager(fileList)
    }

    private fun getFilesForDir(directory: String): Flow<List<FileItem>> {
        return bleViewModel.getRequestApi().request(main {
            storageListRequest = listRequest {
                path = directory
            }
        }).map {
            it.storageListResponse.fileList.map { file ->
                FileItem(
                    file.name,
                    file.type == Storage.File.FileType.DIR,
                    file.size.toLong()
                )
            }
        }.runningReduce { accumulator, value -> accumulator.plus(value) }
    }

    companion object {
        const val EXTRA_DEVICE_KEY = "device_id"
        const val EXTRA_DIRECTORY_KEY = "directory"
    }

    private fun getDeviceId(): String {
        return arguments?.get(EXTRA_DEVICE_KEY) as String
    }

    private fun getDirectory(): String {
        return arguments?.get(EXTRA_DIRECTORY_KEY) as String
    }
}