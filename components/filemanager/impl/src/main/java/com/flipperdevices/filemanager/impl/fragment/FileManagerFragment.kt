package com.flipperdevices.filemanager.impl.fragment

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.view.ComposeFragment
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.filemanager.impl.composable.ComposableFileManager
import com.flipperdevices.filemanager.impl.di.FileManagerComponent
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModelFactory
import com.github.terrakok.cicerone.Router
import java.io.File
import javax.inject.Inject

class FileManagerFragment : ComposeFragment() {
    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screenProvider: FileManagerScreenProvider

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

        ComposableFileManager(fileList) {
            val newPath = File(getDirectory(), it.fileName).absolutePath
            router.navigateTo(screenProvider.fileManager(newPath))
        }
    }

    companion object {
        const val EXTRA_DIRECTORY_KEY = "directory"
    }

    private fun getDirectory(): String {
        return arguments?.get(EXTRA_DIRECTORY_KEY) as String
    }
}
