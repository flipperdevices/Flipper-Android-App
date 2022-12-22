package com.flipperdevices.keyscreen.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.github.terrakok.cicerone.Screen

interface KeyScreenApi {
    fun getKeyScreenScreen(keyPath: FlipperKeyPath): Screen

    @Composable
    fun KeyCard(key: FlipperKeyParsed, deleted: Boolean, modifier: Modifier)

    @Composable
    fun KeyCardLoading(modifier: Modifier)
}
