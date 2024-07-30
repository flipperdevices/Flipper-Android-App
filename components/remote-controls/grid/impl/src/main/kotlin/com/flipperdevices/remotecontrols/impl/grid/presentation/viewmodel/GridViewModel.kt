package com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.infrared.editor.core.parser.InfraredKeyParser
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.PagesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Suppress("LongParameterList")
class GridViewModel @AssistedInject constructor(
    private val pagesRepository: PagesRepository,
    private val simpleKeyApi: SimpleKeyApi,
    private val updateKeyApi: UpdateKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    @Assisted private val param: GridScreenDecomposeComponent.Param,
    @Assisted private val onIrFileLoaded: (String) -> Unit,
    @Assisted private val onUiLoaded: (PagesLayout) -> Unit
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "GridViewModel"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val FlipperKey.stringContent: String
        get() = this.keyContent.openStream().reader().readText()
    private val FlipperFile.stringContent: String
        get() = this.content.openStream().reader().readText()

    private val localFlipperKey = flow {
        val key = simpleKeyApi.getKey(
            FlipperKeyPath(
                path = FlipperFilePath(
                    folder = "${FlipperKeyType.INFRARED.flipperDir}/temp/${param.ifrFileId}",
                    nameWithExtension = "${param.ifrFileId}.ir"
                ),
                deleted = false
            )
        )
        emit(key)
    }.flowOn(FlipperDispatchers.workStealingDispatcher)
        .shareIn(
            viewModelScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed()
        )

    private val localUiFile = localFlipperKey
        .map { it?.additionalFiles.orEmpty() }
        .map { additionalFiles ->
            info { "#localUiFile additionalFiles: ${additionalFiles.size}" }
            additionalFiles.firstNotNullOfOrNull {
                runCatching {
                    json.decodeFromString<PagesLayout>(it.stringContent)
                }.getOrNull()
            }
        }

    private val isDownloaded = combine(
        flow = localFlipperKey,
        flow2 = localUiFile,
        transform = { localSignalFile, localUiFile ->
            info { "#isDownloaded -> localSignalFile=${localSignalFile == null} localUiFile=${localUiFile == null}" }
            localSignalFile != null && localUiFile != null
        }
    )

    private suspend fun loadIrFileContent(): String {
        return localFlipperKey.first()
            ?.stringContent
            ?: pagesRepository.fetchKeyContent(param.ifrFileId)
                .onFailure { _state.emit(State.Error) }
                .onFailure { throwable -> error(throwable) { "#tryLoad could not load key content" } }
                .onSuccess(onIrFileLoaded)
                .getOrNull()
                .orEmpty()
    }

    fun tryLoad() {
        viewModelScope.launch {
            val remotesRaw = loadIrFileContent()
            val remotes = remotesRaw
                .let(FlipperFileFormat::fromFileContent)
                .let(InfraredKeyParser::mapParsedKeyToInfraredRemotes)
            val pagesLayout = localUiFile.first()
                ?: pagesRepository.fetchDefaultPageLayout(ifrFileId = param.ifrFileId)
                    .onSuccess(onUiLoaded)
                    .onFailure { _state.emit(State.Error) }
                    .onFailure { throwable -> error(throwable) { "#tryLoad could not load ui model" } }
                    .getOrNull() ?: return@launch
            _state.emit(
                State.Loaded(
                    pagesLayout = pagesLayout,
                    remotes = remotes.toImmutableList(),
                    remotesRaw = remotesRaw,
                    isDownloaded = isDownloaded.first()
                )
            )
        }
    }

    fun delete() {
        val state = state.value as? GridViewModel.State.Loaded ?: return
        viewModelScope.launch {
            val flipperFilePath = FlipperFilePath(
                folder = "${FlipperKeyType.INFRARED.flipperDir}/temp/${param.ifrFileId}",
                nameWithExtension = "${param.ifrFileId}.ir"
            )
            deleteKeyApi.markDeleted(flipperFilePath)
            deleteKeyApi.deleteMarkedDeleted(flipperFilePath)
            _state.emit(state.copy(isDownloaded = false))
        }
    }

    fun saveSignal() {
        val state = state.value as? GridViewModel.State.Loaded ?: return
        viewModelScope.launch {
            val flipperFilePath = FlipperFilePath(
                folder = "${FlipperKeyType.INFRARED.flipperDir}/temp/${param.ifrFileId}",
                nameWithExtension = "${param.ifrFileId}.ir"
            )
            val flipperKeyPath = FlipperKeyPath(
                path = flipperFilePath,
                deleted = false
            )
            val additionalFiles = listOf(
                FlipperFile(
                    path = FlipperFilePath(
                        folder = "/temp/${param.ifrFileId}",
                        nameWithExtension = "template.ui.json"
                    ),
                    content = FlipperKeyContent.RawData(
                        json.encodeToString(state.pagesLayout).toByteArray()
                    )
                )
            )
            val existingKey = simpleKeyApi.getKey(flipperKeyPath)
            val key = existingKey ?: FlipperKey(
                mainFile = FlipperFile(
                    path = flipperFilePath,
                    content = FlipperKeyContent.RawData(state.remotesRaw.toByteArray())
                ),
                synchronized = true,
                deleted = false,
            )
            if (existingKey == null) simpleKeyApi.insertKey(key = key)
            updateKeyApi.updateKey(key, key.copy(additionalFiles = additionalFiles))
            _state.emit(state.copy(isDownloaded = true))
        }
    }

    init {
        tryLoad()
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(
            val pagesLayout: PagesLayout,
            val remotes: ImmutableList<InfraredRemote>,
            val remotesRaw: String,
            val isDownloaded: Boolean
        ) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            param: GridScreenDecomposeComponent.Param,
            onIrFileLoaded: (String) -> Unit,
            onUiLoaded: (PagesLayout) -> Unit
        ): GridViewModel
    }
}
