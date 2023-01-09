package com.flipperdevices.archive.category.model

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import kotlinx.collections.immutable.ImmutableList

sealed class CategoryState {
    object Loading : CategoryState()

    class Loaded(
        val keys: ImmutableList<Pair<FlipperKeyParsed, FlipperKey>>
    ) : CategoryState()
}
