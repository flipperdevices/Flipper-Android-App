package com.flipperdevices.keyscreen.impl.api

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.warn
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.model.DeleteState
import com.flipperdevices.keyscreen.model.FavoriteState
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.keyscreen.model.ShareState
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.nfceditor.api.NfcEditorApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.gulya.anvil.assisted.ContributesAssistedFactory
import java.util.concurrent.atomic.AtomicBoolean

@ContributesAssistedFactory(AppGraph::class, KeyStateHelperApi.Builder::class)
@Suppress("LongParameterList")
class KeyStateHelperImpl @AssistedInject constructor(
    @Assisted keyPath: FlipperKeyPath,
    @Assisted private val scope: CoroutineScope,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val favoriteApi: FavoriteApi,
    private val keyParser: KeyParser,
    private val metricApi: MetricApi,
    private val updaterKeyApi: UpdateKeyApi,
    private val keyEditorApi: NfcEditorApi,
    private val emulateHelper: EmulateHelper
) : KeyStateHelperApi {
    private val keyScreenState = MutableStateFlow<KeyScreenState>(KeyScreenState.InProgress)
    private val restoreInProgress = AtomicBoolean(false)

    init {
        scope.launch { loadFileAsFlow(keyPath) }
    }

    override fun getKeyScreenState(): StateFlow<KeyScreenState> = keyScreenState.asStateFlow()

    override fun setFavorite(isFavorite: Boolean) {
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready || state.favoriteState == FavoriteState.PROGRESS) {
            warn { "We skip setFavorite, because state is $state" }
            return
        }

        keyScreenState.update {
            if (it is KeyScreenState.Ready) it.copy(favoriteState = FavoriteState.PROGRESS) else it
        }

        scope.launch {
            favoriteApi.setFavorite(state.flipperKey.getKeyPath(), isFavorite)
            keyScreenState.update {
                if (it is KeyScreenState.Ready) {
                    it.copy(
                        favoriteState = if (isFavorite) {
                            FavoriteState.FAVORITE
                        } else {
                            FavoriteState.NOT_FAVORITE
                        }
                    )
                } else {
                    it
                }
            }
        }
    }

    override fun onDelete(onEndAction: () -> Unit) {
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready || state.deleteState == DeleteState.PROGRESS) {
            warn { "We skip onDelete, because state is $state" }
            return
        }
        val newState = state.copy(deleteState = DeleteState.PROGRESS)
        val isStateSaved = keyScreenState.compareAndSet(state, newState)
        if (!isStateSaved) {
            onDelete(onEndAction)
            return
        }

        scope.launch {
            if (state.flipperKey.deleted) {
                deleteKeyApi.deleteMarkedDeleted(state.flipperKey.path)
            } else {
                deleteKeyApi.markDeleted(state.flipperKey.path)
            }
            withContext(Dispatchers.Main) {
                onEndAction()
            }
        }
    }

    override fun onRestore(onEndAction: () -> Unit) {
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready) {
            warn { "We skip onRestore, because state is $state" }
            return
        }

        if (!restoreInProgress.compareAndSet(false, true)) {
            return
        }

        scope.launch {
            deleteKeyApi.restore(state.flipperKey.path)
            withContext(Dispatchers.Main) {
                onEndAction()
            }
        }
    }

    override fun onOpenEdit(onEndAction: (FlipperKeyPath) -> Unit) {
        metricApi.reportSimpleEvent(SimpleEvent.OPEN_EDIT)
        val currentState = keyScreenState.value
        if (currentState is KeyScreenState.Ready) {
            val flipperKeyPath = currentState.flipperKey.getKeyPath()
            onEndAction(flipperKeyPath)
        }
    }

    private suspend fun loadFileAsFlow(keyPathNotNull: FlipperKeyPath) {
        updaterKeyApi.subscribeOnUpdatePath(keyPathNotNull).flatMapLatest {
            combine(
                simpleKeyApi.getKeyAsFlow(it),
                emulateHelper.getCurrentEmulatingKey()
            ) { flipperKey, currentEmulateConfig -> flipperKey to currentEmulateConfig }
        }.collectLatest { (flipperKey, currentEmulateConfig) ->
            if (flipperKey == null) {
                keyScreenState.update {
                    KeyScreenState.Error(R.string.keyscreen_error_notfound_key)
                }
                return@collectLatest
            }

            val parsedKey = keyParser.parseKey(flipperKey)
            val isFavorite = favoriteApi.isFavorite(flipperKey.getKeyPath())
            val isSupportEditing = keyEditorApi.isSupportedByNfcEditor(parsedKey)
            if (!isSupportEditing) {
                keyEditorApi.reportUnsupportedFormat(parsedKey)
            }
            val emulateConfig = getEmulateConfig(
                flipperKey = flipperKey,
                parsedKey = parsedKey
            )
            keyScreenState.update {
                KeyScreenState.Ready(
                    parsedKey,
                    if (isFavorite) FavoriteState.FAVORITE else FavoriteState.NOT_FAVORITE,
                    ShareState.NOT_SHARING,
                    if (flipperKey.deleted) DeleteState.DELETED else DeleteState.NOT_DELETED,
                    flipperKey,
                    emulateConfig = emulateConfig,
                    isSupportEditing = isSupportEditing,
                    emulatingInProgress = flipperKey.path == currentEmulateConfig?.keyPath
                )
            }
        }
    }

    private fun getEmulateConfig(
        flipperKey: FlipperKey,
        parsedKey: FlipperKeyParsed
    ): EmulateConfig? {
        val keyType = flipperKey.flipperKeyType

        if (keyType == null || keyType == FlipperKeyType.INFRARED) {
            return null
        }

        val timeout = if (parsedKey is FlipperKeyParsed.SubGhz &&
            parsedKey.totalTimeMs != null
        ) {
            parsedKey.totalTimeMs
        } else {
            null
        }

        return EmulateConfig(
            keyType = keyType,
            keyPath = flipperKey.path,
            minEmulateTime = timeout,
        )
    }
}
