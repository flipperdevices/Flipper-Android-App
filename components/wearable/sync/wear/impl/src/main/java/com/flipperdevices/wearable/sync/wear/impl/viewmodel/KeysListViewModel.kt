package com.flipperdevices.wearable.sync.wear.impl.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.concurrent.futures.await
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.widget.ConfirmationOverlay
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.wearable.sync.common.WearableSyncItem
import com.flipperdevices.wearable.sync.wear.api.FindPhoneApi
import com.flipperdevices.wearable.sync.wear.api.FindPhoneState
import com.flipperdevices.wearable.sync.wear.impl.R
import com.flipperdevices.wearable.sync.wear.impl.model.FlipperWearKey
import com.flipperdevices.wearable.sync.wear.impl.model.KeysListState
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent.TYPE_CHANGED
import com.google.android.gms.wearable.DataEvent.TYPE_DELETED
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

private const val ANDROID_MARKET_APP_URI = "market://details?id=com.flipperdevices.app"

class KeysListViewModel @Inject constructor(
    private val application: Application,
    findPhoneApi: FindPhoneApi
) : DecomposeViewModel(), LogTagProvider, DataClient.OnDataChangedListener {
    override val TAG = "KeysListViewModel"

    private val dataClient by lazy { Wearable.getDataClient(application) }
    private val remoteActivityHelper by lazy { RemoteActivityHelper(application) }

    private val keysListFlow = MutableStateFlow<ImmutableList<FlipperWearKey>>(persistentListOf())
    private val keysListStateFlow = MutableStateFlow<KeysListState>(KeysListState.Loading)

    fun getKeysListFlow(): StateFlow<KeysListState> = keysListStateFlow.asStateFlow()

    init {
        dataClient.addListener(this)

        combine(
            keysListFlow,
            findPhoneApi.getState()
        ) { keys, phoneState ->
            info { "Combine $keys $phoneState" }
            when (phoneState) {
                is FindPhoneState.Founded -> keysListStateFlow.emit(KeysListState.Loaded(keys))
                FindPhoneState.Loading -> keysListStateFlow.emit(KeysListState.Loading)
                FindPhoneState.NotFound -> keysListStateFlow.emit(KeysListState.PhoneNotFound)
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            requestFirstData()
        }
    }

    private suspend fun requestFirstData() {
        val dataItem = dataClient.dataItems.await().map {
            WearableSyncItem.fromDataItem(it)
        }.filterNotNull().map { FlipperWearKey(it) }

        keysListFlow.emit(dataItem.toImmutableList())
    }

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(datas: DataEventBuffer) = keysListFlow.update { keyState ->
        info { "Handle changes $datas" }
        val mapByPath = keyState.associateByTo(HashMap()) { it.path }
        datas.forEach {
            val syncItem = try {
                WearableSyncItem.fromDataItem(it.dataItem)
            } catch (throwable: Exception) {
                error(throwable) { "While parse $it" }
                null
            } ?: return@forEach
            val flipperWearKey = FlipperWearKey(syncItem)
            when (it.type) {
                TYPE_CHANGED -> mapByPath[flipperWearKey.path] = flipperWearKey
                TYPE_DELETED -> mapByPath.remove(flipperWearKey.path)
                else -> error { "Can't found action for event $it" }
            }
        }
        return@update mapByPath.values.toImmutableList()
    }

    fun openStore() {
        info { "#openAppInStoreOnPhone" }
        val intent = when (PhoneTypeHelper.getPhoneDeviceType(application)) {
            PhoneTypeHelper.DEVICE_TYPE_ANDROID -> {
                info { "DEVICE_TYPE_ANDROID" }
                // Create Remote Intent to open Play Store listing of app on remote device.
                Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(ANDROID_MARKET_APP_URI))
            }

            else -> {
                info { "DEVICE_TYPE_ERROR_UNKNOWN" }
                return
            }
        }

        viewModelScope.launch {
            try {
                remoteActivityHelper.startRemoteActivity(intent).await()

                CurrentActivityHolder.getCurrentActivity()?.let {
                    it.toast(R.string.install_app_done)
                    ConfirmationOverlay().showOn(it)
                }
            } catch (cancellationException: CancellationException) {
                // Request was cancelled normally
                throw cancellationException
            } catch (throwable: Throwable) {
                error(throwable) { "Error execute $intent" }
                CurrentActivityHolder.getCurrentActivity()?.let {
                    it.toast(R.string.install_app_fail)
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .showOn(it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dataClient.removeListener(this)
    }
}
