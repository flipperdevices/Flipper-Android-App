package com.flipperdevices.firstpair.impl.api

import ComposableSearchingView
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.firstpair.impl.viewmodels.connecting.PairDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider
import tangle.viewmodel.compose.tangleViewModel

class DeviceScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onFinishConnect: () -> Unit,
    @Assisted private val onHelpClick: () -> Unit,
    private val bleDeviceViewModelProvider: Provider<BLEDeviceViewModel>,
    private val pairDeviceViewModelProvider: Provider<PairDeviceViewModel>
) : ComponentContext by componentContext, DecomposeComponent {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val pairViewModel: PairDeviceViewModel = viewModelWithFactory(key = null) {
            pairDeviceViewModelProvider.get()
        }
        val bleDeviceViewModel: BLEDeviceViewModel = viewModelWithFactory(key = null) {
            bleDeviceViewModelProvider.get()
        }
        ComposableSearchingView(
            onHelpClicking = onHelpClick,
            onFinishConnection = onFinishConnect,
            onBack = onBack::invoke,
            bleDeviceViewModel = bleDeviceViewModel,
            pairViewModel = pairViewModel
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            onFinishConnect: () -> Unit,
            onHelpClick: () -> Unit
        ): DeviceScreenDecomposeComponent
    }
}