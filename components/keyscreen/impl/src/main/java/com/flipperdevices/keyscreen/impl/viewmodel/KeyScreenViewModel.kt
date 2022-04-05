package com.flipperdevices.keyscreen.impl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.warn
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.model.DeleteState
import com.flipperdevices.keyscreen.impl.model.FavoriteState
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.model.ShareState
import com.github.terrakok.cicerone.Router
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyScreenViewModel(
    private val keyPath: FlipperKeyPath?,
    application: Application
) : AndroidViewModel(application), LogTagProvider {
    override val TAG = "KeyScreenViewModel"

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var deleteKeyApi: DeleteKeyApi

    @Inject
    lateinit var favoriteApi: FavoriteApi

    @Inject
    lateinit var keyParser: KeyParser

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
    }

    private val keyScreenState = MutableStateFlow<KeyScreenState>(KeyScreenState.InProgress)
    private val shareDelegate = ShareDelegate(application, keyParser)
    private val saveDelegate = SaveDelegate()
    private val restoreInProgress = AtomicBoolean(false)

    init {
        viewModelScope.launch {
            val keyPathNotNull = if (keyPath == null) {
                keyScreenState.update { KeyScreenState.Error(R.string.keyscreen_error_keypath) }
                return@launch
            } else keyPath
            val flipperKey = simpleKeyApi.getKey(keyPathNotNull)
            if (flipperKey == null) {
                keyScreenState.update {
                    KeyScreenState.Error(R.string.keyscreen_error_notfound_key)
                }
                return@launch
            }

            loadKey(flipperKey)
            return@launch
        }
    }

    fun getKeyScreenState(): StateFlow<KeyScreenState> = keyScreenState

    fun setFavorite(isFavorite: Boolean) {
        val keyPathNotNull = keyPath ?: return
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready || state.favoriteState == FavoriteState.PROGRESS) {
            warn { "We skip setFavorite, because state is $state" }
            return
        }

        keyScreenState.update {
            if (it is KeyScreenState.Ready) it.copy(favoriteState = FavoriteState.PROGRESS) else it
        }

        viewModelScope.launch {
            favoriteApi.setFavorite(keyPathNotNull, isFavorite)
            keyScreenState.update {
                if (it is KeyScreenState.Ready) it.copy(
                    favoriteState = if (isFavorite) {
                        FavoriteState.FAVORITE
                    } else FavoriteState.NOT_FAVORITE
                ) else it
            }
        }
    }

    fun onOpenEdit() {
        keyScreenState.update {
            if (it is KeyScreenState.Ready) {
                KeyScreenState.Editing(it.flipperKey, it.parsedKey)
            } else it
        }
    }

    fun onBack(): Boolean {
        val currentState = keyScreenState.value
        if (currentState !is KeyScreenState.Editing) {
            return false
        }
        val isStateSaved = keyScreenState.compareAndSet(currentState, KeyScreenState.InProgress)
        if (!isStateSaved) {
            return onBack()
        }
        viewModelScope.launch {
            loadKey(currentState.flipperKey)
        }
        return true
    }

    fun onEditFinished(newFlipperKey: FlipperKey) {
        val currentState = keyScreenState.value
        if (currentState !is KeyScreenState.Editing) {
            return
        }
        val isStateSaved = keyScreenState.compareAndSet(currentState, KeyScreenState.InProgress)
        if (!isStateSaved) {
            onEditFinished(newFlipperKey)
            return
        }

        viewModelScope.launch {
            @Suppress("TooGenericExceptionCaught")
            try {
                saveDelegate.onEditSaveInternal(
                    currentState.flipperKey,
                    newFlipperKey
                )
                loadKey(newFlipperKey)
            } catch (e: Exception) {
                error(e) { "Error while save key after editing" }
                getApplication<Application>().toast(R.string.keyscreen_save_error)
                val parsed = keyParser.parseKey(newFlipperKey)
                keyScreenState.emit(KeyScreenState.Editing(newFlipperKey, parsed))
            }
        }
    }

    fun onShare() {
        val keyPathNotNull = keyPath ?: return
        val state = keyScreenState.value
        if (state !is KeyScreenState.Ready || state.shareState == ShareState.PROGRESS) {
            warn { "We skip onShare, because state is $state" }
            return
        }
        keyScreenState.update {
            if (it is KeyScreenState.Ready) it.copy(shareState = ShareState.PROGRESS) else it
        }

        viewModelScope.launch {
            val flipperKey = simpleKeyApi.getKey(keyPathNotNull)
            if (flipperKey == null) {
                keyScreenState.update {
                    KeyScreenState.Error(R.string.keyscreen_error_notfound_key)
                }
                return@launch
            }
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
            if (state.flipperKey.path.deleted) {
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

    private suspend fun loadKey(flipperKey: FlipperKey) {
        val parsedKey = keyParser.parseKey(flipperKey)
        val isFavorite = favoriteApi.isFavorite(flipperKey.path)
        keyScreenState.update {
            KeyScreenState.Ready(
                parsedKey,
                if (isFavorite) FavoriteState.FAVORITE else FavoriteState.NOT_FAVORITE,
                ShareState.NOT_SHARING,
                if (flipperKey.path.deleted) DeleteState.DELETED else DeleteState.NOT_DELETED,
                flipperKey
            )
        }
    }
}
