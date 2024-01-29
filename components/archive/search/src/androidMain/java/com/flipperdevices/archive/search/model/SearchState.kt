package com.flipperdevices.archive.search.model

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import kotlinx.collections.immutable.ImmutableList

sealed class SearchState {
    object Loading : SearchState()

    class Loaded(
        val keys: ImmutableList<Pair<FlipperKeyParsed, FlipperKey>>
    ) : SearchState()
}
