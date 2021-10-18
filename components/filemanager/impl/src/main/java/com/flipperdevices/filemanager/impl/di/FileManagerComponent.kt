package com.flipperdevices.filemanager.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.impl.fragment.FileManagerFragment
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface FileManagerComponent {
    fun inject(fileManagerFragment: FileManagerFragment)
}
