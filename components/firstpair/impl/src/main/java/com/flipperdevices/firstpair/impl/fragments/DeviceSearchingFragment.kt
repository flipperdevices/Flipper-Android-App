package com.flipperdevices.firstpair.impl.fragments

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.firstpair.impl.composable.searching.ComposableSearchingScreen
import com.flipperdevices.firstpair.impl.di.FirstPairComponent
import com.flipperdevices.firstpair.impl.model.SearchingContent
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.flipperdevices.firstpair.impl.viewmodels.SearchStateBuilder
import com.flipperdevices.firstpair.impl.viewmodels.connecting.PairDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.PermissionChangeDetectBroadcastReceiver
import com.flipperdevices.firstpair.impl.viewmodels.searching.PermissionStateBuilder
import com.flipperdevices.singleactivity.api.SingleActivityApi
import javax.inject.Inject

class DeviceSearchingFragment :
    ComposeFragment(),
    LogTagProvider {
    override val TAG = "DeviceSearchingFragment"

    @Inject
    lateinit var firstPairStorage: FirstPairStorage

    @Inject
    lateinit var singleActivityApi: SingleActivityApi

    init {
        ComponentHolder.component<FirstPairComponent>().inject(this)
    }

    private val viewModelSearch by viewModels<BLEDeviceViewModel>()
    private val viewModelConnecting by viewModels<PairDeviceViewModel>()

    private lateinit var searchStateBuilder: SearchStateBuilder
    private var permissionChangeBroadcastReceiver: PermissionChangeDetectBroadcastReceiver? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchStateBuilder = SearchStateBuilder(
            context = context,
            PermissionStateBuilder(fragment = this, context = context),
            viewModelSearch,
            viewModelConnecting,
            lifecycleScope
        )
        permissionChangeBroadcastReceiver = PermissionChangeDetectBroadcastReceiver(
            searchStateBuilder
        )
    }

    @Composable
    override fun RenderView() {
        val state by searchStateBuilder.getState().collectAsState()
        info { "New state is $state" }

        (state.content as? SearchingContent.Finished)?.let {
            finishConnection(it.deviceId)
        }

        ComposableSearchingScreen(
            state = state,
            onBack = {
                requireRouter().exit()
            },
            onHelpClicking = {
                // TODO
            },
            onSkipConnection = {
                finishConnection()
            },
            onDeviceClick = {
                viewModelConnecting.startConnectToDevice(it)
            },
            onRefreshSearching = {
                searchStateBuilder.resetByUser()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        searchStateBuilder.invalidate()
        val activityNotNull = activity ?: return
        permissionChangeBroadcastReceiver?.register(activityNotNull)
    }

    override fun onPause() {
        super.onPause()
        val activityNotNull = activity ?: return
        permissionChangeBroadcastReceiver?.unregister(activityNotNull)
    }

    private fun finishConnection(deviceId: String? = null) {
        viewModelConnecting.close()
        firstPairStorage.markDeviceSelected(deviceId)
        singleActivityApi.open()
    }
}
