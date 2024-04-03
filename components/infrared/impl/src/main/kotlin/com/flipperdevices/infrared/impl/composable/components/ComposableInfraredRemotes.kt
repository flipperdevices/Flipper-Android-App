package com.flipperdevices.infrared.impl.composable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.infrared.impl.R
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

    if (remoteConfigs.isEmpty()) {
        EmptyInfraredRemotesContent()
    } else {
        PopulatedInfraredRemotesContent(
            remoteConfigs = remoteConfigs,
            keyEmulateContent = keyEmulateContent
        )
    }
}

@Composable
private fun PopulatedInfraredRemotesContent(
    remoteConfigs: ImmutableList<EmulateConfig>,
    keyEmulateContent: @Composable (EmulateConfig) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        items(
            items = remoteConfigs,
        ) { config ->
            keyEmulateContent(config)
        }
    }
}

@Composable
private fun EmptyInfraredRemotesContent() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.infrared_buttons_empty),
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text16,
        textAlign = TextAlign.Center
    )
}

private fun KeyScreenState.Ready.toEmulateConfigs(): ImmutableList<EmulateConfig> {
    val infraredParsed = this.parsedKey as? FlipperKeyParsed.Infrared ?: return persistentListOf()

    return infraredParsed.remotes.mapIndexed { index, name ->
        EmulateConfig(
            keyType = FlipperKeyType.INFRARED,
            keyPath = this.flipperKey.path,
            args = name,
            index = index
        )
    }.toImmutableList()
}
