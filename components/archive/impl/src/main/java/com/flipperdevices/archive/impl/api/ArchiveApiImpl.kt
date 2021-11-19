package com.flipperdevices.archive.impl.api

import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.archive.impl.fragments.ArchiveFragment
import com.flipperdevices.core.di.AppGraph
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ArchiveApiImpl @Inject constructor() : ArchiveApi {
    override fun getArchiveScreen(): Screen {
        return FragmentScreen { ArchiveFragment() }
    }
}
