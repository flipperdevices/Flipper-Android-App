package com.flipperdevices.firstpair.impl.api

import ComposableSearchingView
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.firstpair.impl.viewmodels.connecting.PairDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

fun interface OnClickHelp {
    operator fun invoke()
}

class DeviceScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onFinishConnect: () -> Unit,
    @Assisted private val onHelpClick: OnClickHelp,
    private val bleDeviceViewModelProvider: Provider<BLEDeviceViewModel>,
    private val pairDeviceViewModelProvider: Provider<PairDeviceViewModel>
) : ScreenDecomposeComponent(componentContext) {

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
            onHelpClicking = onHelpClick::invoke,
            onFinishConnection = onFinishConnect,
            onBack = onBack::invoke,
            bleDeviceViewModel = bleDeviceViewModel,
            pairViewModel = pairViewModel,
            lifecycleOwner = this
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            onFinishConnect: () -> Unit,
            onHelpClick: OnClickHelp
        ): DeviceScreenDecomposeComponent
    }
}
