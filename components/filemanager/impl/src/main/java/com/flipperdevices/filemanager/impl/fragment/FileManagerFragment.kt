package com.flipperdevices.filemanager.impl.fragment

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.core.view.ComposeFragment
import com.flipperdevices.filemanager.impl.composable.ComposableFileManager
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.Storage
import com.flipperdevices.protobuf.storage.listRequest
import com.flipperdevices.service.FlipperViewModel
import com.flipperdevices.service.FlipperViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.launch
import timber.log.Timber

class FileManagerFragment : ComposeFragment() {
    private val stateFlow = MutableStateFlow<List<FileItem>>(emptyList())

    private val bleViewModel by activityViewModels<FlipperViewModel> {
        FlipperViewModelFactory(requireActivity().application, getDeviceId())
    }

    @Composable
    override fun renderView() {
        val fileList by stateFlow.collectAsState(emptyList())

        ComposableFileManager(fileList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            getFilesForDir(getDirectory()).collect {
                stateFlow.emit(it)
            }
        }
    }

    private fun getFilesForDir(directory: String): Flow<List<FileItem>> {
        return bleViewModel.getRequestApi().request(main {
            storageListRequest = listRequest {
                path = directory
            }
        }).map {
            Timber.i("FileManagerFragment#${directory}")
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