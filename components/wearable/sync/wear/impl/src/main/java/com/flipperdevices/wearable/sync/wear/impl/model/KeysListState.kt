package com.flipperdevices.wearable.sync.wear.impl.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

sealed class KeysListState {
    object Loading : KeysListState()

    @Stable
    data class Loaded(val keys: ImmutableList<FlipperWearKey>) : KeysListState()
}
