package com.flipperdevices.nfceditor.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.flipperdevices.nfceditor.impl.fragments.NfcEditorFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class NfcEditorApiImpl @Inject constructor() : NfcEditorApi {
    override fun getNfcEditorScreen(): Screen {
        return FragmentScreen { NfcEditorFragment() }
    }
}
