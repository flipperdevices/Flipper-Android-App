package com.flipperdevices.filemanager.impl.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.withArgs
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerSaveWithDialog
import com.flipperdevices.filemanager.impl.di.FileManagerComponent
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModelFactory
import com.flipperdevices.share.api.ReceiveApi
import javax.inject.Inject

class FileManagerSaveFragment : ComposeFragment() {
    @Inject
    lateinit var screenProvider: FileManagerScreenProvider

    @Inject
    lateinit var receiveApi: ReceiveApi

    private val directory
        get() = arguments?.getString(EXTRA_DIRECTORY_KEY)!!

    private val fileUri
        get() = arguments?.getParcelable<Uri>(EXTRA_FILE_URI_KEY)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<FileManagerComponent>().inject(this)
    }

    private val viewModel by viewModels<FileManagerViewModel>() {
        FileManagerViewModelFactory(directory)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    @Composable
    override fun renderView() {
        val fileList by viewModel.getFileList().collectAsState()

        ComposableFileManagerSaveWithDialog(
            receiveApi,
            fileUri,
            directory,
            fileList,
            onOpenFolder = { fileItem ->
                requireRouter().navigateTo(
                    screenProvider.saveWithFileManager(
                        fileUri,
                        fileItem.path
                    )
                )
            }
        )
    }

    companion object {
        private const val EXTRA_DIRECTORY_KEY = "directory"
        private const val EXTRA_FILE_URI_KEY = "file_uri"

        fun newInstance(directory: String, uri: Uri): Fragment {
            return FileManagerSaveFragment().withArgs {
                putString(EXTRA_DIRECTORY_KEY, directory)
                putParcelable(EXTRA_FILE_URI_KEY, uri)
            }
        }
    }
}
