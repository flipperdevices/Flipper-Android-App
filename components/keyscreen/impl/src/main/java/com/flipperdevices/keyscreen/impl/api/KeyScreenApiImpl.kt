package com.flipperdevices.keyscreen.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.keyscreen.impl.composable.card.ComposableKeyCard
import com.flipperdevices.keyscreen.impl.fragments.KeyScreenFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyScreenApiImpl @Inject constructor() : KeyScreenApi {
    override fun getKeyScreenScreen(keyPath: FlipperKeyPath): Screen {
        return FragmentScreen { KeyScreenFragment.getInstance(keyPath) }
    }

    @Composable
    override fun KeyCard(key: FlipperKeyParsed) {
        ComposableKeyCard(parsedKey = key)
    }
}
