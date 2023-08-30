package com.flipperdevices.wearable.sync.wear.impl.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

sealed class KeysListState {
    data object Loading : KeysListState()
    data object PhoneNotFound : KeysListState()

    @Stable
    data class Loaded(val keys: ImmutableList<FlipperWearKey>) : KeysListState()
}
