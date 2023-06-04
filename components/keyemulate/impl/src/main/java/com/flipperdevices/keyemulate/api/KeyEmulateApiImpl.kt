package com.flipperdevices.keyemulate.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyemulate.composable.ComposableSimpleEmulateButton
import com.flipperdevices.keyemulate.composable.ComposableSubGhzSendButton
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyEmulateApiImpl @Inject constructor() : KeyEmulateApi {
    @Composable
    override fun ComposableEmulateButton(modifier: Modifier, flipperKey: FlipperKey) {
        val fileType = flipperKey.flipperKeyType

        if (flipperKey.deleted) return

        when (fileType) {
            FlipperKeyType.SUB_GHZ -> ComposableSubGhzSendButton(
                modifier = modifier,
                flipperKey = flipperKey
            )
            FlipperKeyType.I_BUTTON,
            FlipperKeyType.RFID,
            FlipperKeyType.NFC -> ComposableSimpleEmulateButton(
                modifier = modifier,
                flipperKey = flipperKey
            )
            FlipperKeyType.INFRARED, null -> {}
        }
    }
}
