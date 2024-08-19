package com.flipperdevices.remotecontrols.impl.grid.local.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.infrared.editor.core.parser.InfraredKeyParser
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

class LocalGridViewModel @AssistedInject constructor(
    @Assisted private val keyPath: FlipperKeyPath,
    private val updaterKeyApi: UpdateKeyApi,
    private val simpleKeyApi: SimpleKeyApi,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "LocalGridViewModel"
    private val json: Json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val keyFlow = updaterKeyApi
        .subscribeOnUpdatePath(keyPath)
        .flatMapLatest(simpleKeyApi::getKeyAsFlow)

    private val keyPathFlow = keyFlow.map {
        it?.getKeyPath()
    }

    private val uiFlow = keyFlow
        .map { it?.additionalFiles.orEmpty() }
        .map { additionalFiles ->
            additionalFiles.firstNotNullOfOrNull { fFile ->
                val text = fFile.content.openStream().reader().readText()
                runCatching {
                    json.decodeFromString<PagesLayout>(text)
                }.getOrNull()
            }
        }.onEach { info { "#uiFlow $it" } }
    private val remotesFlow = keyFlow.map {
        it?.keyContent
            ?.openStream()
            ?.reader()
            ?.readText()
            ?.let(FlipperFileFormat::fromFileContent)
            ?.let(InfraredKeyParser::mapParsedKeyToInfraredRemotes)
            ?.toImmutableList()
    }.onEach { info { "#remotesFlow $it" } }
    val state = combine(
        flow = uiFlow,
        flow2 = remotesFlow,
        flow3 = keyPathFlow,
        transform = { pagesLayout, remotes, keyPath ->
            if (pagesLayout != null && remotes != null && keyPath != null) {
                State.Loaded(
                    pagesLayout = pagesLayout,
                    remotes = remotes,
                    keyPath = keyPath
                )
            } else {
                State.Error
            }
        }
    ).stateIn(viewModelScope, SharingStarted.Eagerly, State.Loading)

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(
            val pagesLayout: PagesLayout,
            val remotes: ImmutableList<InfraredRemote>,
            val keyPath: FlipperKeyPath
        ) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            keyPath: FlipperKeyPath,
        ): LocalGridViewModel
    }
}
