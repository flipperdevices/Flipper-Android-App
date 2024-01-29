package com.flipperdevices.archive.category.model

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import kotlinx.collections.immutable.ImmutableList

sealed class CategoryState {
    data object Loading : CategoryState()

    data class Loaded(
        val keys: ImmutableList<Pair<FlipperKeyParsed, FlipperKey>>
    ) : CategoryState()
}
