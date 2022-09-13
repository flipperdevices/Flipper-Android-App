package com.flipperdevices.wearable.sync.wear.impl.model

import androidx.compose.runtime.Stable

sealed class KeysListState {
    object Loading : KeysListState()

    @Stable
    data class Loaded(val keys: List<FlipperWearKey>) : KeysListState()
}
