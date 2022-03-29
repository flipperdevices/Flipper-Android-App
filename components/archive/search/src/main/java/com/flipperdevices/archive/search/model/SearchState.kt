package com.flipperdevices.archive.search.model

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

sealed class SearchState {
    object Loading : SearchState()

    class Loaded(val keys: List<Pair<FlipperKeyParsed, FlipperKey>>) : SearchState()
}
