package com.flipperdevices.keyscreen.impl.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.api.EXTRA_KEY_PATH
import com.flipperdevices.keyscreen.impl.model.DeleteState
import com.flipperdevices.keyscreen.impl.model.FavoriteState
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.model.ShareState
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("LongParameterList")
class KeyScreenViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    keyPath: FlipperKeyPath,
    application: Application,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val favoriteApi: FavoriteApi,
    private val keyParser: KeyParser,
    private val metricApi: MetricApi,
    private val updaterKeyApi: UpdateKeyApi
) : AndroidLifecycleViewModel(application), LogTagProvider {
    override val TAG = "KeyScreenViewModel"

    private val keyScreenState = MutableStateFlow<KeyScreenState>(KeyScreenState.InProgress)
    private val restoreInProgress = AtomicBoolean(false)
    private var loadKeyJob: Job? = null

    init {
        loadKeyJob = viewModelScope.launch {
            loadFileAsFlow(keyPath)
        }
    }

    fun getKeyScreenState(): StateFlow<KeyScreenState> = keyScreenState

    fun setFavorite(isFavorite: Boolean) {
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready || state.favoriteState == FavoriteState.PROGRESS) {
            warn { "We skip setFavorite, because state is $state" }
            return
        }

        keyScreenState.update {
            if (it is KeyScreenState.Ready) it.copy(favoriteState = FavoriteState.PROGRESS) else it
        }

        viewModelScope.launch {
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

    fun onOpenEdit(onEndAction: (FlipperKeyPath) -> Unit) {
        metricApi.reportSimpleEvent(SimpleEvent.OPEN_EDIT)
        val currentState = keyScreenState.value
        if (currentState is KeyScreenState.Ready) {
            val flipperKeyPath = currentState.flipperKey.getKeyPath()
            onEndAction(flipperKeyPath)
        }
    }

    fun onDelete(onEndAction: () -> Unit) {
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

        viewModelScope.launch {
            if (state.flipperKey.deleted) {
                deleteKeyApi.deleteMarkedDeleted(state.flipperKey.path)
            } else {
                deleteKeyApi.markDeleted(state.flipperKey.path)
            }
            onEndAction()
        }
    }

    fun onRestore(onEndAction: () -> Unit) {
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready) {
            warn { "We skip onRestore, because state is $state" }
            return
        }

        if (!restoreInProgress.compareAndSet(false, true)) {
            return
        }

        viewModelScope.launch {
            deleteKeyApi.restore(state.flipperKey.path)
            onEndAction()
        }
    }

    fun openNfcEditor(onEndAction: (FlipperKeyPath) -> Unit) {
        when (val state = keyScreenState.value) {
            is KeyScreenState.Ready -> {
                metricApi.reportSimpleEvent(SimpleEvent.OPEN_NFC_DUMP_EDITOR)
                val flipperKey = state.flipperKey
                val flipperKeyPath = flipperKey.getKeyPath()
                onEndAction(flipperKeyPath)
            }
            else -> {}
        }
    }

    private suspend fun loadFileAsFlow(keyPathNotNull: FlipperKeyPath) {
        updaterKeyApi.subscribeOnUpdatePath(keyPathNotNull).flatMapLatest {
            simpleKeyApi.getKeyAsFlow(it)
        }.collectLatest { flipperKey ->
            if (flipperKey == null) {
                keyScreenState.update {
                    KeyScreenState.Error(R.string.keyscreen_error_notfound_key)
                }
                return@collectLatest
            }

            val parsedKey = keyParser.parseKey(flipperKey)
            val isFavorite = favoriteApi.isFavorite(flipperKey.getKeyPath())
            keyScreenState.update {
                KeyScreenState.Ready(
                    parsedKey,
                    if (isFavorite) FavoriteState.FAVORITE else FavoriteState.NOT_FAVORITE,
                    ShareState.NOT_SHARING,
                    if (flipperKey.deleted) DeleteState.DELETED else DeleteState.NOT_DELETED,
                    flipperKey
                )
            }
        }
    }
}
