package com.flipperdevices.keyemulate.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.keyemulate.composable.ComposableInfraredSendButton
import com.flipperdevices.keyemulate.composable.ComposableSimpleEmulateButton
import com.flipperdevices.keyemulate.composable.ComposableSubGhzSendButton
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.viewmodel.InfraredViewModel
import com.flipperdevices.keyemulate.viewmodel.SimpleEmulateViewModel
import com.flipperdevices.keyemulate.viewmodel.SubGhzViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class)
class KeyEmulateApiImpl @Inject constructor(
    private val infraredViewModel: Provider<InfraredViewModel>,
    private val simpleEmulateViewModel: Provider<SimpleEmulateViewModel>,
    private val subGhzViewModel: Provider<SubGhzViewModel>
) : KeyEmulateApi {
    @Composable
    override fun ComposableEmulateButton(
        modifier: Modifier,
        emulateConfig: EmulateConfig,
        isSynchronized: Boolean
    ) {
        when (emulateConfig.keyType) {
            FlipperKeyType.SUB_GHZ -> ComposableSubGhzSendButton(
                modifier = modifier,
                emulateConfig = emulateConfig,
                isSynchronized = isSynchronized,
                emulateViewModel = viewModelWithFactory(key = null) {
                    subGhzViewModel.get()
                }
            )

            FlipperKeyType.I_BUTTON,
            FlipperKeyType.RFID,
            FlipperKeyType.NFC -> ComposableSimpleEmulateButton(
                modifier = modifier,
                emulateConfig = emulateConfig,
                isSynchronized = isSynchronized,
                emulateViewModel = viewModelWithFactory(key = null) {
                    simpleEmulateViewModel.get()
                }
            )

            FlipperKeyType.INFRARED -> ComposableInfraredSendButton(
                modifier = modifier,
                emulateConfig = emulateConfig,
                isSynchronized = isSynchronized,
                emulateViewModel = viewModelWithFactory(key = null) {
                    infraredViewModel.get()
                }
            )
        }
    }
}
