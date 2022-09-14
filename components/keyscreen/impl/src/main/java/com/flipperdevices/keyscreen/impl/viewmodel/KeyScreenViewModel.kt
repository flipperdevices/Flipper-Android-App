package com.flipperdevices.keyscreen.impl.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.keyedit.api.KeyEditApi
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.model.DeleteState
import com.flipperdevices.keyscreen.impl.model.FavoriteState
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.model.ShareState
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.github.terrakok.cicerone.Router
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyScreenViewModel(
    keyPath: FlipperKeyPath?,
    application: Application
) : AndroidLifecycleViewModel(application), LogTagProvider {
    override val TAG = "KeyScreenViewModel"

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var deleteKeyApi: DeleteKeyApi

    @Inject
    lateinit var favoriteApi: FavoriteApi

    @Inject
    lateinit var keyParser: KeyParser

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var metricApi: MetricApi

    @Inject
    lateinit var globalCicerone: CiceroneGlobal

    @Inject
    lateinit var nfcEditorApi: NfcEditorApi

    @Inject
    lateinit var updaterKeyApi: UpdateKeyApi

    @Inject
    lateinit var keyEditApi: KeyEditApi

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
    }

    private val keyScreenState = MutableStateFlow<KeyScreenState>(KeyScreenState.InProgress)
    private val shareDelegate = ShareDelegate(application, keyParser)
    private val restoreInProgress = AtomicBoolean(false)
    private var loadKeyJob: Job? = null

    init {
        loadKeyJob = viewModelScope.launch {
            val keyPathNotNull = if (keyPath == null) {
                keyScreenState.update { KeyScreenState.Error(R.string.keyscreen_error_keypath) }
                return@launch
            } else keyPath
            loadFileAsFlow(keyPathNotNull)
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
                if (it is KeyScreenState.Ready) it.copy(
                    favoriteState = if (isFavorite) {
                        FavoriteState.FAVORITE
                    } else FavoriteState.NOT_FAVORITE
                ) else it
            }
        }
    }

    fun onOpenEdit(router: Router) {
        metricApi.reportSimpleEvent(SimpleEvent.OPEN_EDIT)
        val currentState = keyScreenState.value
        if (currentState is KeyScreenState.Ready) {
            router.navigateTo(keyEditApi.getScreen(currentState.flipperKey.getKeyPath()))
        }
    }

    fun onNfcEdit(flipperKey: FlipperKey) {
        globalCicerone.getRouter().navigateTo(nfcEditorApi.getNfcEditorScreen(flipperKey))
    }

    fun onShare() {
        metricApi.reportSimpleEvent(SimpleEvent.OPEN_SHARE)
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready || state.shareState == ShareState.PROGRESS) {
            warn { "We skip onShare, because state is $state" }
            return
        }
        keyScreenState.update {
            if (it is KeyScreenState.Ready) it.copy(shareState = ShareState.PROGRESS) else it
        }

        viewModelScope.launch {
            val flipperKey = state.flipperKey
            shareDelegate.share(flipperKey)
            keyScreenState.update {
                if (it is KeyScreenState.Ready) it.copy(shareState = ShareState.NOT_SHARING) else it
            }
        }
    }

    fun onDelete(router: Router) {
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready || state.deleteState == DeleteState.PROGRESS) {
            warn { "We skip onDelete, because state is $state" }
            return
        }
        val newState = state.copy(deleteState = DeleteState.PROGRESS)
        val isStateSaved = keyScreenState.compareAndSet(state, newState)
        if (!isStateSaved) {
            onDelete(router)
            return
        }

        viewModelScope.launch {
            if (state.flipperKey.deleted) {
                deleteKeyApi.deleteMarkedDeleted(state.flipperKey.path)
            } else deleteKeyApi.markDeleted(state.flipperKey.path)
            router.exit()
        }
    }

    fun onRestore(router: Router) {
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
            router.exit()
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
