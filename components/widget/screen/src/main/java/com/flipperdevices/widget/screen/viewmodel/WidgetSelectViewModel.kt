package com.flipperdevices.widget.screen.viewmodel

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.widget.api.WidgetApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class WidgetSelectViewModel @AssistedInject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val favoriteApi: FavoriteApi,
    private val synchronizationApi: SynchronizationApi,
    private val widgetDataApi: WidgetDataApi,
    private val widgetApi: WidgetApi,
    @Assisted private val widgetId: Int
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "WidgetSelectViewModel"

    private val keys = MutableStateFlow<ImmutableList<FlipperKey>>(persistentListOf())
    private val favoriteKeys = MutableStateFlow<ImmutableList<FlipperKey>>(persistentListOf())
    private val synchronizationState =
        MutableStateFlow<SynchronizationState>(SynchronizationState.NotStarted)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            simpleKeyApi.getExistKeysAsFlow(null)
                .combine(favoriteApi.getFavoritesFlow()) { keyList, favoriteKeysList ->
                    val favoriteKeyPaths = favoriteKeysList.map { it.path }.toSet()
                    val keysExceptFavorite =
                        keyList.filterNot { favoriteKeyPaths.contains(it.path) }
                    keys.emit(keysExceptFavorite.toImmutableList())
                    favoriteKeys.emit(favoriteKeysList.toImmutableList())
                }.collect()
        }
        synchronizationApi.getSynchronizationState().onEach {
            synchronizationState.emit(it)
        }.launchIn(viewModelScope)
    }

    fun getKeysFlow(): StateFlow<ImmutableList<FlipperKey>> = keys
    fun getFavoriteKeysFlow(): StateFlow<ImmutableList<FlipperKey>> = favoriteKeys
    fun getSynchronizationFlow(): StateFlow<SynchronizationState> = synchronizationState

    fun refresh() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun onSelectKey(keyPath: FlipperKeyPath) {
        val currentActivity = CurrentActivityHolder.getCurrentActivity()
        info { "#onSelectKey for $widgetId $keyPath" }
        viewModelScope.launch {
            widgetDataApi.updateKeyForWidget(widgetId, keyPath)
            widgetApi.resetStateOfWidget(widgetId)
            widgetApi.invalidate()
            notifyActivityAboutResult(currentActivity)

            currentActivity?.finish()
        }
    }

    private fun notifyActivityAboutResult(currentActivity: Activity?) {
        info { "Notify about result for activity $currentActivity and widget $widgetId" }
        currentActivity?.setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(widgetId: Int): WidgetSelectViewModel
    }
}
