package com.flipperdevices.infrared.impl.composable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.model.KeyScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun ComposableInfraredRemotes(
    state: KeyScreenState.Ready,
    keyEmulateContent: @Composable (EmulateConfig) -> Unit
) {
    val remoteConfigs = remember(state) { state.toEmulateConfigs() }

    ComposableInfraredName(keyName = state.flipperKey.path.nameWithoutExtension)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        items(
            items = remoteConfigs,
        ) { config ->
            keyEmulateContent(config)
        }
    }
}

private fun KeyScreenState.Ready.toEmulateConfigs(): ImmutableList<EmulateConfig> {
    val infraredParsed = this.parsedKey as? FlipperKeyParsed.Infrared ?: return persistentListOf()

    return infraredParsed.remotes.map { name ->
        EmulateConfig(
            keyType = FlipperKeyType.INFRARED,
            keyPath = this.flipperKey.path,
            args = name
        )
    }.toImmutableList()
}
