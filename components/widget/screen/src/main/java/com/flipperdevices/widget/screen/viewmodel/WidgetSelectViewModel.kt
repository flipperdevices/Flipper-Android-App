package com.flipperdevices.widget.screen.viewmodel

import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.api.SearchApi
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
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.widget.api.WidgetApi
import com.flipperdevices.widget.screen.fragments.EXTRA_WIDGET_ID_KEY
import com.github.terrakok.cicerone.ResultListener
import com.github.terrakok.cicerone.ResultListenerHandler
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

@Suppress("LongParameterList")
class WidgetSelectViewModel @VMInject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val favoriteApi: FavoriteApi,
    private val searchApi: SearchApi,
    private val synchronizationApi: SynchronizationApi,
    private val widgetDataApi: WidgetDataApi,
    private val widgetApi: WidgetApi,
    private val globalCicerone: CiceroneGlobal,
    @TangleParam(EXTRA_WIDGET_ID_KEY)
    private val widgetId: Int
) : ViewModel(), ResultListener, LogTagProvider {
    override val TAG = "WidgetSelectViewModel"

    private val keys = MutableStateFlow<List<FlipperKey>>(emptyList())
    private val favoriteKeys = MutableStateFlow<List<FlipperKey>>(emptyList())
    private val synchronizationState =
        MutableStateFlow<SynchronizationState>(SynchronizationState.NotStarted)
    private var resultListenerDispatcher: ResultListenerHandler? = null

    init {
        viewModelScope.launch(Dispatchers.Default) {
            simpleKeyApi.getExistKeysAsFlow(null)
                .combine(favoriteApi.getFavoritesFlow()) { keyList, favoriteKeysList ->
                    val favoriteKeyPaths = favoriteKeysList.map { it.path }.toSet()
                    val keysExceptFavorite =
                        keyList.filterNot { favoriteKeyPaths.contains(it.path) }
                    keys.emit(keysExceptFavorite)
                    favoriteKeys.emit(favoriteKeysList)
                }.collect()
        }
        synchronizationApi.getSynchronizationState().onEach {
            synchronizationState.emit(it)
        }.launchIn(viewModelScope)
    }

    fun getKeysFlow(): StateFlow<List<FlipperKey>> = keys
    fun getFavoriteKeysFlow(): StateFlow<List<FlipperKey>> = favoriteKeys
    fun getSynchronizationFlow(): StateFlow<SynchronizationState> = synchronizationState

    fun refresh() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun onOpenSearch(router: Router) {
        resultListenerDispatcher?.dispose()
        resultListenerDispatcher = router.setResultListener(SearchApi.SEARCH_RESULT_KEY, this)
        router.navigateTo(searchApi.getSearchScreen(exitOnOpen = true))
    }

    override fun onResult(data: Any) {
        if (data is FlipperKeyPath) {
            onSelectKey(data)
        }
    }

    fun onSelectKey(keyPath: FlipperKeyPath) {
        info { "#onSelectKey for $widgetId $keyPath" }
        viewModelScope.launch {
            widgetDataApi.updateKeyForWidget(widgetId, keyPath)
            widgetApi.invalidate()
            notifyActivityAboutResult()
            globalCicerone.getRouter().finishChain()
        }
    }

    private fun notifyActivityAboutResult() {
        val currentActivity = CurrentActivityHolder.getCurrentActivity()
        info { "Notify about result for activity $currentActivity and widget $widgetId" }
        currentActivity?.setResult(RESULT_OK, Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        })
    }

    override fun onCleared() {
        super.onCleared()
        resultListenerDispatcher?.dispose()
    }
}
