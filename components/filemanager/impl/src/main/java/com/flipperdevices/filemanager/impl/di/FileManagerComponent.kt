package com.flipperdevices.filemanager.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.impl.fragment.FileManagerFragment
import com.flipperdevices.filemanager.impl.fragment.FileManagerSaveFragment
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface FileManagerComponent {
    fun inject(fragment: FileManagerFragment)
    fun inject(fragment: FileManagerSaveFragment)
    fun inject(viewModel: FileManagerViewModel)
}
