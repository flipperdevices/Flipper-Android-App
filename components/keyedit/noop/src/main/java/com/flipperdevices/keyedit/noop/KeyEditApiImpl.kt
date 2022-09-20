package com.flipperdevices.keyedit.noop

import androidx.fragment.app.Fragment
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditApi
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyEditApiImpl @Inject constructor() : KeyEditApi {
    override fun getScreen(flipperKeyPath: FlipperKeyPath, title: String?) =
        FragmentScreen { Fragment() }

    override fun getScreen(notSavedFlipperKey: NotSavedFlipperKey, title: String?) =
        FragmentScreen { Fragment() }
}
