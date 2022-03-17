package com.flipperdevices.archive.category.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

sealed class CategoryState {
    object Loading : CategoryState()

    class Loaded(val keys: List<Pair<FlipperKeyParsed, FlipperKeyPath>>) : CategoryState()
}
