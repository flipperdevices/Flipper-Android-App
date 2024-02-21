package com.flipperdevices.info.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.info.impl.compose.screens.ComposableFullDeviceInfoScreen
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.BasicInfoViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.FullInfoViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.ShareFullInfoFileViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class FullInfoDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val shareFullInfoViewModelProvider: Provider<ShareFullInfoFileViewModel>,
    private val basicInfoViewModelProvider: Provider<BasicInfoViewModel>,
    private val fullInfoViewModelProvider: Provider<FullInfoViewModel>,
    private val deviceStatusViewModelProvider: Provider<DeviceStatusViewModel>
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        ComposableFullDeviceInfoScreen(
            onBack = onBack::invoke,
            shareViewModel = viewModelWithFactory(key = null) {
                shareFullInfoViewModelProvider.get()
            },
            basicInfoViewModel = viewModelWithFactory(key = null) {
                basicInfoViewModelProvider.get()
            },
            fullInfoViewModel = viewModelWithFactory(key = null) {
                fullInfoViewModelProvider.get()
            },
            deviceStatusViewModel = viewModelWithFactory(key = null) {
                deviceStatusViewModelProvider.get()
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): FullInfoDecomposeComponent
    }
}
