package com.flipperdevices.archive.impl.di

import com.flipperdevices.archive.impl.fragments.ArchiveFragment
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface ArchiveComponent {
    fun inject(fragment: ArchiveFragment)
}
