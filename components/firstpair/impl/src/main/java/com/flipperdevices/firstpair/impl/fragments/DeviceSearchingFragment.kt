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
import com.flipperdevices.firstpair.impl.model.SearchingState
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.flipperdevices.firstpair.impl.viewmodels.SearchStateBuilder
import com.flipperdevices.firstpair.impl.viewmodels.connecting.PairDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchStateBuilder = SearchStateBuilder(
            PermissionStateBuilder(fragment = this, context = context),
            viewModelSearch,
            viewModelConnecting,
            lifecycleScope
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
            state = SearchingState(
                showSearching = true,
                showHelp = true,
                content = SearchingContent.Searching
            ),
            onBack = {
                requireRouter().exit()
            },
            onHelpClicking = {
                TODO()
            },
            onSkipConnection = {
                finishConnection()
            },
            onDeviceClick = {
                viewModelConnecting.startConnectToDevice(it.device)
            },
            onRefreshSearching = {
                viewModelSearch.stopScan()
                viewModelSearch.startScanIfNotYet()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        searchStateBuilder.invalidate()
    }

    private fun finishConnection(deviceId: String? = null) {
        viewModelConnecting.close()
        firstPairStorage.markDeviceSelected(deviceId)
        singleActivityApi.open()
    }
}
