package com.flipperdevices.filemanager.impl.fragment

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerWithDialog
import com.flipperdevices.filemanager.impl.di.FileManagerComponent
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModelFactory
import com.flipperdevices.share.api.ShareApi
import javax.inject.Inject

class FileManagerFragment : ComposeFragment() {
    @Inject
    lateinit var screenProvider: FileManagerScreenProvider

    @Inject
    lateinit var shareApi: ShareApi

    private val viewModel by viewModels<FileManagerViewModel>() {
        FileManagerViewModelFactory(getDirectory())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<FileManagerComponent>().inject(this)
    }

    @Composable
    override fun renderView() {
        val fileList by viewModel.getFileList().collectAsState()

        ComposableFileManagerWithDialog(fileList, shareApi) { fileItem ->
            requireRouter().navigateTo(screenProvider.fileManager(fileItem.path))
        }
    }

    companion object {
        const val EXTRA_DIRECTORY_KEY = "directory"
    }

    private fun getDirectory(): String {
        return arguments?.get(EXTRA_DIRECTORY_KEY) as String
    }
}
