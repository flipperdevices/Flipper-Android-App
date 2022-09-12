package com.flipperdevices.wearable.sync.wear.impl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.sync.common.WearableSyncItem
import com.flipperdevices.wearable.sync.wear.impl.model.FlipperWearKey
import com.flipperdevices.wearable.sync.wear.impl.model.KeysListState
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent.TYPE_CHANGED
import com.google.android.gms.wearable.DataEvent.TYPE_DELETED
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class KeysListViewModel(
    application: Application
) : AndroidViewModel(application), LogTagProvider, DataClient.OnDataChangedListener {
    override val TAG = "KeysListViewModel"
    private val dataClient by lazy { Wearable.getDataClient(application) }

    private val keysListStateFlow = MutableStateFlow<KeysListState>(KeysListState.Loading)

    fun getKeysListFlow(): StateFlow<KeysListState> = keysListStateFlow

    init {
        dataClient.addListener(this)

        viewModelScope.launch {
            requestFirstData()
        }
    }

    private suspend fun requestFirstData() {
        val dataItem = dataClient.dataItems.await().map {
            WearableSyncItem.fromDataItem(it)
        }.filterNotNull().map { FlipperWearKey(it) }

        keysListStateFlow.emit(KeysListState.Loaded(dataItem))
    }

    override fun onDataChanged(datas: DataEventBuffer) = keysListStateFlow.update { keyState ->
        info { "Handle changes $datas" }
        if (keyState !is KeysListState.Loaded) {
            info { "Can't apply $datas because state is $keyState" }
            return@update keyState
        }
        val mapByPath = keyState.keys.associateByTo(HashMap()) { it.path }
        datas.forEach {
            val syncItem = WearableSyncItem.fromDataItem(it.dataItem) ?: return@forEach
            val flipperWearKey = FlipperWearKey(syncItem)
            when (it.type) {
                TYPE_CHANGED -> mapByPath[flipperWearKey.path] = flipperWearKey
                TYPE_DELETED -> mapByPath.remove(flipperWearKey.path)
                else -> error { "Can't found action for event $it" }
            }
        }
        return@update KeysListState.Loaded(mapByPath.values.toList())
    }

    override fun onCleared() {
        super.onCleared()
        dataClient.removeListener(this)
    }
}
