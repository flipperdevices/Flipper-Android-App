package com.flipperdevices.keyemulate.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.keyemulate.composable.ComposableInfraredSendButton
import com.flipperdevices.keyemulate.composable.ComposableSimpleEmulateButton
import com.flipperdevices.keyemulate.composable.ComposableSubGhzSendButton
import com.flipperdevices.keyemulate.viewmodel.InfraredViewModel
import com.flipperdevices.keyemulate.viewmodel.SimpleEmulateViewModel
import com.flipperdevices.keyemulate.viewmodel.SubGhzViewModel
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider

@ContributesBinding(AppGraph::class)
class KeyEmulateApiImpl @Inject constructor(
    private val infraredViewModel: Provider<InfraredViewModel>,
    private val simpleEmulateViewModel: Provider<SimpleEmulateViewModel>,
    private val subGhzViewModel: Provider<SubGhzViewModel>
) : KeyEmulateApi {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun ComposableEmulateButton(
        modifier: Modifier,
        emulateConfig: EmulateConfig,
        isSynchronized: Boolean,
        componentContext: ComponentContext
    ) {
        when (emulateConfig.keyType) {
            FlipperKeyType.SUB_GHZ -> ComposableSubGhzSendButton(
                modifier = modifier,
                emulateConfig = emulateConfig,
                isSynchronized = isSynchronized,
                emulateViewModel = componentContext.viewModelWithFactory(key = null) {
                    subGhzViewModel.invoke()
                }
            )

            FlipperKeyType.I_BUTTON,
            FlipperKeyType.RFID,
            FlipperKeyType.NFC -> ComposableSimpleEmulateButton(
                modifier = modifier,
                emulateConfig = emulateConfig,
                isSynchronized = isSynchronized,
                emulateViewModel = componentContext.viewModelWithFactory(key = null) {
                    simpleEmulateViewModel.invoke()
                }
            )

            FlipperKeyType.INFRARED -> ComposableInfraredSendButton(
                modifier = modifier,
                emulateConfig = emulateConfig,
                isSynchronized = isSynchronized,
                emulateViewModel = componentContext.viewModelWithFactory(key = null) {
                    infraredViewModel.invoke()
                }
            )
        }
    }
}
