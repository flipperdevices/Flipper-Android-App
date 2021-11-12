package com.flipperdevices.filemanager.impl.navigation

import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.withArgs
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.filemanager.impl.fragment.FileManagerFragment
import com.flipperdevices.filemanager.impl.fragment.FileManagerSaveFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class FileManagerScreenProviderImpl @Inject constructor() : FileManagerScreenProvider {
    override fun fileManager(path: String): Screen {
        return FragmentScreen("FileManager_$path") {
            FileManagerFragment().withArgs {
                putString(FileManagerFragment.EXTRA_DIRECTORY_KEY, path)
            }
        }
    }

    override fun saveWithFileManager(uri: Uri, path: String): Screen {
        return FragmentScreen("FileManagerSave_$path") {
            FileManagerSaveFragment.newInstance(path, uri)
        }
    }
}
