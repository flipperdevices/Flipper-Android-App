package com.flipperdevices.remotecontrols.impl.grid.local.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.infrared.editor.core.parser.InfraredKeyParser
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import com.flipperdevices.keyscreen.model.FavoriteState
import com.flipperdevices.keyscreen.model.KeyScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

class LocalGridViewModel @AssistedInject constructor(
    @Assisted private val keyPath: FlipperKeyPath,
    keyStateHelperApi: KeyStateHelperApi.Builder,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "LocalGridViewModel"
    private val json: Json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val keyStateHelper = keyStateHelperApi.build(keyPath, viewModelScope)

    val state = keyStateHelper.getKeyScreenState()
        .map {
            when (it) {
                is KeyScreenState.Error -> State.Error
                KeyScreenState.InProgress -> State.Loading
                is KeyScreenState.Ready -> {
                    val keyPath = it.flipperKey.getKeyPath()
                    val pagesLayout = it.flipperKey.additionalFiles.firstNotNullOfOrNull { fFile ->
                        val text = fFile.content.openStream().reader().readText()
                        runCatching {
                            json.decodeFromString<PagesLayout>(text)
                        }.getOrNull()
                    }
                    val remotes = it.flipperKey.keyContent
                        .openStream()
                        .reader()
                        .readText()
                        .let(FlipperFileFormat::fromFileContent)
                        .let(InfraredKeyParser::mapParsedKeyToInfraredRemotes)
                        .toImmutableList()
                    if (pagesLayout == null) {
                        State.Error
                    } else {
                        State.Loaded(
                            pagesLayout = pagesLayout,
                            remotes = remotes,
                            keyPath = keyPath,
                            isFavorite = it.favoriteState == FavoriteState.FAVORITE
                        )
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, State.Loading)

    fun onRename(onEndAction: (FlipperKeyPath) -> Unit) = keyStateHelper.onOpenEdit(onEndAction)

    fun onDelete(onEndAction: () -> Unit) = keyStateHelper.onDelete(onEndAction)

    fun toggleFavorite() {
        val state = keyStateHelper.getKeyScreenState().value
        val readyState = state as? KeyScreenState.Ready ?: return
        val isFavorite = readyState.favoriteState == FavoriteState.FAVORITE
        keyStateHelper.setFavorite(!isFavorite)
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(
            val pagesLayout: PagesLayout,
            val remotes: ImmutableList<InfraredRemote>,
            val keyPath: FlipperKeyPath,
            val isFavorite: Boolean
        ) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            keyPath: FlipperKeyPath,
        ): LocalGridViewModel
    }
}
