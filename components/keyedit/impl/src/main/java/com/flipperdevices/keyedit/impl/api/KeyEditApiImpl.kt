package com.flipperdevices.keyedit.impl.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditApi
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.keyedit.impl.fragment.KeyEditFragment
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyEditApiImpl @Inject constructor() : KeyEditApi {
    override fun getScreen(flipperKeyPath: FlipperKeyPath, title: String?): Screen {
        return FragmentScreen {
            KeyEditFragment.getInstance(EditableKey.Existed(flipperKeyPath), title)
        }
    }

    override fun getScreen(notSavedFlipperKey: NotSavedFlipperKey, title: String?): Screen {
        return FragmentScreen {
            KeyEditFragment.getInstance(EditableKey.Limb(notSavedFlipperKey), title)
        }
    }
}
